import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {BaseChartDirective} from "ng2-charts";
import {RestService} from "../../../logic/datahandler/rest.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";

@Component({
  selector: 'app-scatter-plot',
  templateUrl: './mixed-plot.component.html',
  styleUrls: ['../../../lib/plot-component-util/styles/plot-component-styles.scss']
})
export class ScatterPlotComponent implements OnInit {

  @ViewChild(BaseChartDirective)
  private chart: { refresh: () => void } = {refresh: () => console.log('chart not initialized yet')};

  public readonly chartType: ChartType = 'scatter';
  public chartTitle = '';
  public chartData: ChartDataSets[] = Array();
  public xLabel: string = 'x';
  public yLabel: string = 'y';
  public yType: ScaleType = 'logarithmic';
  public xType: ScaleType = 'linear';
  public pointSize: number = 2;
  public fontSize: number = 12;

  public chartOptions: ChartOptions = {
    title: {
      display: true,
      text: this.chartTitle,
      fontSize: this.fontSize,
    },
    responsive: true,
    animation: {
      animateScale: false,
      animateRotate: false,
      duration: 0,
    },
    maintainAspectRatio: true,
    legend: {
      display: true,
      labels: {
        fontSize: this.fontSize,
      },
    },
    events: ['click'],
    elements: {
      point: {
        radius: this.pointSize,
      },
      line: {
        borderWidth: 2,
        tension: 0,
        fill: false
      }
    },
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: this.yLabel,
          fontSize: this.fontSize,
        },
        ticks: {
          fontSize: this.fontSize,
        },
        type: this.yType
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: this.xLabel,
          fontSize: this.fontSize,
        },
        ticks: {
          fontSize: this.fontSize,
        },
        type: this.xType,
      }]
    },
  };


  constructor(private readonly route: ActivatedRoute, private readonly dataHandler: RestService) {
  }

  ngOnInit() {
    this.readParams(this.route.queryParamMap);
  }

  readParams(params: Observable<ParamMap>) {
    params.subscribe(p => {
      const config: PlotConfiguration | undefined = PlotUtils.parsePlotConfig(p);
      if (config === undefined) {
        return;
      }
      this.chartTitle = config.labelForTitle;
      this.xLabel = config.labelForXAxis;
      this.yLabel = config.labelForYAxis;

      //will be used to add opacity
      function hexToRGB(hex: string, alpha: number) {
        var r = parseInt(hex.slice(1, 3), 16),
            g = parseInt(hex.slice(3, 5), 16),
            b = parseInt(hex.slice(5, 7), 16);

        if (alpha) {
            return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
        } else {
            return "rgb(" + r + ", " + g + ", " + b + ")";
        }
    }
      this.dataHandler.getPlotData(config).subscribe(data => {
        let tempChartData = PlotUtils.colorizeDataSet(data.datasets);

        // will contain the real data push the new datasets to it
        let chartDataClone = [];
        console.log(tempChartData.length);
        for(let i = 0; i < tempChartData.length; i++) {
          let len = tempChartData[i].data?.length;
          if(len == undefined) len  =0;

          //declaring new arrays
          let newMax = [];
          let newMin= [];
          let newAvg = [];

          //goes through all the x,y data ${rate} element at a time
          let rate = 100;
          for(let j = 0; j< len; j+=rate){
            let tempData = tempChartData[i].data;
            if(tempData == undefined) tempData = [];
            let tData = tempData[j];

            if(tData == undefined) tData = {x:0, y:0};


            // @ts-ignore
            let max = tData.y;
            // @ts-ignore
            let min = tData.y;
            // @ts-ignore
            let sumx = tData.x;
            // @ts-ignore
            let sumy = tData.y;
            let lengthLim = j + rate;
            if(lengthLim > tempData.length) lengthLim = tempData.length;

            for(let k= j+1; k < lengthLim; k++){
              // @ts-ignore
              if(tempData[k].y < min) min = tempData[k];
              // @ts-ignore
              if(tempData[k].y > max) max = tempData[k];
              // @ts-ignore
              sumx+= tempData[k].x;
              // @ts-ignore
              sumy+= tempData[k].y;
            }
            newMax.push({x:sumx/rate, y:max});
            newMin.push({x:sumx/rate, y:min});
            newAvg.push({x:sumx/rate, y:sumy/rate})
          }

          // @ts-ignore
          chartDataClone.push({type:'line', data: newMin,tension: 0.5,fill:'+1',backgroundColor: hexToRGB(tempChartData[i].backgroundColor, 0.2)   , color: tempChartData[i].color, pointBorderColor:tempChartData[i].pointBorderColor,borderColor:tempChartData[i].borderColor, label:tempChartData[i].label });
          // @ts-ignore
          chartDataClone.push({type:'line' ,data: newMax,tension: 0.5,fill:'-1',backgroundColor:hexToRGB(tempChartData[i].backgroundColor, 0.2), color: tempChartData[i].color, pointBorderColor:tempChartData[i].pointBorderColor,borderColor:tempChartData[i].borderColor,label:tempChartData[i].label});
          // @ts-ignore
          chartDataClone.push({type:'line' ,data: newAvg,tension: 0.1,fill:'false' ,backgroundColor:hexToRGB(tempChartData[i].backgroundColor, 0.5), color: tempChartData[i].color, pointBorderColor:tempChartData[i].pointBorderColor,borderColor:tempChartData[i].borderColor,label:tempChartData[i].label});

        }

        this.chartData = chartDataClone;
        this.updateChart();
      });
    });
  }

  updateChart() {
    if (this.chartOptions.title?.text !== undefined) {
      this.chartOptions.title.text = this.chartTitle;
      this.chartOptions.title.fontSize = this.fontSize;
    }
    if (this.chartOptions.scales?.xAxes !== undefined && this.chartOptions.scales.xAxes.length > 0) {
      this.chartOptions.scales.xAxes[0].type = this.xType;
      if (this.chartOptions.scales.xAxes[0].scaleLabel) {
        this.chartOptions.scales.xAxes[0].scaleLabel.labelString = this.xLabel;
        this.chartOptions.scales.xAxes[0].scaleLabel.fontSize = this.fontSize;
      }
      if (this.chartOptions.scales.xAxes[0].ticks !== undefined) {
        this.chartOptions.scales.xAxes[0].ticks.fontSize = this.fontSize;
      }
    }
    if (this.chartOptions.scales?.yAxes !== undefined && this.chartOptions.scales.yAxes.length > 0) {
      this.chartOptions.scales.yAxes[0].type = this.yType;
      if (this.chartOptions.scales.yAxes[0].scaleLabel) {
        this.chartOptions.scales.yAxes[0].scaleLabel.labelString = this.yLabel;
        this.chartOptions.scales.yAxes[0].scaleLabel.fontSize = this.fontSize;
      }
      if (this.chartOptions.scales.yAxes[0].ticks !== undefined) {
        this.chartOptions.scales.yAxes[0].ticks.fontSize = this.fontSize;
      }
    }
    if (this.chartOptions.elements?.point?.radius !== undefined) {
      this.chartOptions.elements.point.radius = this.pointSize;
    }
    if (this.chartOptions.legend?.labels?.fontSize !== undefined) {
      this.chartOptions.legend.labels.fontSize = this.fontSize;
    }

    this.chart.refresh();
  }

  downloadCanvas(event: MouseEvent) {
    PlotUtils.downloadCanvas(event, `${this.chartType}-plot.png`);
  }
}
