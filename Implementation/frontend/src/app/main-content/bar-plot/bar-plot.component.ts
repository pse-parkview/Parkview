import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {BaseChartDirective} from "ng2-charts";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {
  X_AXIS_PLOT_OPTION_NAME,
  Y_AXIS_PLOT_OPTION_NAME
} from "../../../logic/plothandler/interfaces/available-plot-types";

@Component({
  selector: 'app-bar-plot',
  templateUrl: './bar-plot.component.html',
  styleUrls: ['./bar-plot.component.scss']
})
export class BarPlotComponent implements OnInit {

  @ViewChild(BaseChartDirective)
  private chart: { refresh: () => void } = {refresh: () => console.log('chart not initialized yet')};

  public readonly chartType: ChartType = 'bar';
  public chartData: ChartDataSets[] = Array();
  public xLabel: string = 'x';
  public yLabel: string = 'y';
  public yType: ScaleType = 'logarithmic';
  public xType: ScaleType = 'linear';

  public chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: true,
    legend: {display: true},
    events: ['click'],
    elements: {},
    scales: {
      yAxes: [{
        stacked: true,
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
      if (p.has('benchmark') && p.has('commits') && p.has('devices') && p.has('plotType')) {
        // Get additional configuration
        const extraOptions: { [key: string]: string } = {};
        p.keys.filter(k => !['benchmark', 'commits', 'devices', 'plotType'].includes(k))
          .forEach((k => extraOptions[k] = p.get(k) as string));
        // Build request
        let config: PlotConfiguration = {
          benchmark: p.get("benchmark") as string,
          commits: p.getAll("commits"),
          devices: p.getAll("devices"),
          plotType: p.get("plotType") as string,
          options: extraOptions
        };

        // Make request and read data
        this.dataHandler.getPlotData(config).subscribe(d => this.chartData = d);
      }
      const xLabelParam = p.get(X_AXIS_PLOT_OPTION_NAME);
      this.xLabel = xLabelParam ? xLabelParam : 'x';
      const yLabelParam = p.get(Y_AXIS_PLOT_OPTION_NAME);
      this.yLabel = yLabelParam ? yLabelParam : 'y';
      this.updateChart();
    });
  }

  getData() {
    return [{}];
  }

  updateChart() {
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
    this.chart.refresh();
  }

}
