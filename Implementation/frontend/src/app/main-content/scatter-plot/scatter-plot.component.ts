import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {BaseChartDirective} from "ng2-charts";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";

@Component({
  selector: 'app-scatter-plot',
  templateUrl: './scatter-plot.component.html',
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
  public colors: string[] = ['#BF616A', '#D08770', '#EBCB8B', '#A3BE8C', '#B48EAD'];

  public chartOptions: ChartOptions = {
    title: {
      display: true,
      text: this.chartTitle,
    },
    responsive: true,
    animation: {
      animateScale: false,
      animateRotate: false,
      duration: 0,
    },
    maintainAspectRatio: true,
    legend: {display: true},
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
          labelString: this.yLabel
        },
        type: this.yType
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: this.xLabel
        },
        type: this.xType,
      }]
    },
  };


  constructor(private readonly route: ActivatedRoute, private readonly dataHandler: DataService) {
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

      this.dataHandler.getPlotData(config).subscribe(data => {
        const d = data.datasets
        for (let i = 0; i < d.length; i++) {
          let color = this.getColor(i);
          d[i].pointBackgroundColor = color;
          d[i].pointBorderColor = color;
          d[i].borderColor = color;
        }
        this.chartData = d;
      });
      this.updateChart();
    });
  }

  updateChart() {
    if (this.chartOptions.title?.text !== undefined) {
      this.chartOptions.title.text = this.chartTitle;
    }
    if (this.chartOptions.scales?.xAxes !== undefined && this.chartOptions.scales.xAxes.length > 0) {
      this.chartOptions.scales.xAxes[0].type = this.xType;
      if (this.chartOptions.scales.xAxes[0].scaleLabel) {
        this.chartOptions.scales.xAxes[0].scaleLabel.labelString = this.xLabel;
      }
    }
    if (this.chartOptions.scales?.yAxes !== undefined && this.chartOptions.scales.yAxes.length > 0) {
      this.chartOptions.scales.yAxes[0].type = this.yType;
      if (this.chartOptions.scales.yAxes[0].scaleLabel) {
        this.chartOptions.scales.yAxes[0].scaleLabel.labelString = this.yLabel;
      }
    }
    if (this.chartOptions.elements?.point?.radius !== undefined) {
      this.chartOptions.elements.point.radius = this.pointSize;
    }
    this.chart.refresh();
  }

  getColor(i: number) {
    if (i < this.colors.length) {
      return this.colors[i];
    } else {
      return `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 255)`;
    }
  }

  downloadCanvas(event: any) {
    let anchor = event.target;
    let canvas = document.getElementById('canvas') as HTMLCanvasElement;
    let ctx = canvas.getContext('2d');

    ctx!.globalCompositeOperation = 'destination-over';
    ctx!.fillStyle = 'white';
    ctx!.fillRect(0, 0, canvas.width, canvas.height);

    anchor.href = canvas.toDataURL();
    anchor.download = `${this.chartType}-plot.png`;
  }
}
