import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {RestService} from "../../../logic/datahandler/rest.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {BaseChartDirective} from "ng2-charts";
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";

@Component({
  selector: 'app-line-plot',
  templateUrl: './line-plot.component.html',
  styleUrls: ['../../../lib/plot-component-util/styles/plot-component-styles.scss']
})
export class LinePlotComponent implements OnInit {

  @ViewChild(BaseChartDirective)
  private chart: { refresh: () => void } = {refresh: () => console.log('chart not initialized yet')};

  public readonly chartType: ChartType = 'line';
  public chartData: ChartDataSets[] = Array();
  public chartTitle: string = '';
  public xLabel: string = 'x';
  public yLabel: string = 'y';
  public yType: ScaleType = 'logarithmic';
  public xType: ScaleType = 'linear';
  public fontSize: number = 12;

  public chartOptions: ChartOptions = {
    title: {
      display: true,
      text: '',
      fontSize: this.fontSize,
    },
    responsive: true,
    maintainAspectRatio: true,
    legend: {
      display: true,
      labels: {
        fontSize: this.fontSize,
      }
    },
    events: ['click'],
    elements: {
      point: {
        radius: 0,
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
      if (config.plotType === "Performance Plot") {
        this.yType = "linear";
      }
      this.chartTitle = config.labelForTitle;
      this.xLabel = config.labelForXAxis;
      this.yLabel = config.labelForYAxis;

      this.dataHandler.getPlotData(config).subscribe(data => {
        this.chartData = PlotUtils.colorizeDataSet(data.datasets);
        this.updateChart();
      });
    });
  }

  updateChart() {
    if (this.chartOptions.title?.text !== undefined) {
      this.chartOptions.title.fontSize = this.fontSize;
      this.chartOptions.title.text = this.chartTitle;
    }
    if (this.chartOptions.legend?.labels?.fontSize !== undefined) {
      this.chartOptions.legend.labels.fontSize = this.fontSize;
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
    this.chart.refresh();
  }

}
