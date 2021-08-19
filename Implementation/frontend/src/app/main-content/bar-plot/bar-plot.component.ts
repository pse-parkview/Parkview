import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {BaseChartDirective, Label} from "ng2-charts";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";

@Component({
  selector: 'app-bar-plot',
  templateUrl: './bar-plot.component.html',
  styleUrls: ['../../../lib/plot-component-util/styles/plot-component-styles.scss']
})
export class BarPlotComponent implements OnInit {

  @ViewChild(BaseChartDirective)
  private chart: { refresh: () => void } = {refresh: () => console.log('chart not initialized yet')};

  public readonly chartType: ChartType = 'bar';
  public chartTitle: string = '';
  public chartData: ChartDataSets[] = Array();
  public xLabel: string = 'x';
  public yLabel: string = 'y';
  public yType: ScaleType = 'linear';
  public chartLabels: Label[] = Array();
  public fontSize: number = 12;

  public chartOptions: ChartOptions = {
    title: {
      display: true,
      text: this.chartTitle,
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
    scales: {
      yAxes: [{
        stacked: true,
        scaleLabel: {
          display: true,
          fontSize: this.fontSize,
          labelString: this.yLabel
        },
        ticks: {
          fontSize: this.fontSize,
        },
        type: this.yType
      }],
      xAxes: [{
        stacked: true,
        scaleLabel: {
          display: true,
          fontSize: this.fontSize,
          labelString: this.xLabel
        },
        ticks: {
          fontSize: this.fontSize,
        }
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
        this.chartData = PlotUtils.colorizeDataSet(data.datasets);
        this.chartLabels = data.labels;
        this.updateChart();
      });
    });
  }

  updateChart() {
    if (this.chartOptions.title?.text !== undefined) {
      this.chartOptions.title.text = this.chartTitle;
      this.chartOptions.title.fontSize = this.fontSize;
    }
    if (this.chartOptions.legend?.labels?.fontSize !== undefined) {
      this.chartOptions.legend.labels.fontSize = this.fontSize;
    }
    if (this.chartOptions.scales?.xAxes !== undefined && this.chartOptions.scales.xAxes.length > 0) {
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
        this.chartOptions.scales.yAxes[0].scaleLabel.fontSize = this.fontSize;
        this.chartOptions.scales.yAxes[0].scaleLabel.labelString = this.yLabel;
      }
      if (this.chartOptions.scales.yAxes[0].ticks !== undefined) {
        this.chartOptions.scales.yAxes[0].ticks.fontSize = this.fontSize;
      }
    }
    this.chart.refresh();
  }

  downloadCanvas(event: MouseEvent) {
    PlotUtils.downloadCanvas(event, `${this.chartType}-plot.png`);
  }
}
