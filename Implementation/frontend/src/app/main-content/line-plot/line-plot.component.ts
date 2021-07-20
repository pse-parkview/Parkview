import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType, ScaleType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {BaseChartDirective} from "ng2-charts";

@Component({
  selector: 'app-line-plot',
  templateUrl: './line-plot.component.html',
  styleUrls: ['./line-plot.component.scss']
})
export class LinePlotComponent implements OnInit {

  @ViewChild(BaseChartDirective)
  private chart: { refresh: () => void } = { refresh: () => console.log('chart not initialized yet') };

  public readonly chartType: ChartType = 'line';
  public chartData: ChartDataSets[] = Array();
  public chartTitle: string = '';
  public xLabel: string = 'x';
  public yLabel: string = 'y';
  public yType: ScaleType = 'logarithmic';
  public xType: ScaleType = 'linear';

  public chartOptions: ChartOptions = {
    title: {
      display: true,
      text: ''
    },
    responsive: true,
    maintainAspectRatio: true,
    legend: {display: true},
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
      const mandatoryParams: string[] = [
        'benchmark',
        'commits',
        'devices',
        'plotType',
        'labelForTitle',
        'labelForXAxis',
        'labelForYAxis'
      ];

      if (!mandatoryParams.some(k => !p.has(k))) { // if there arent any mising mandatory keys
        const extraOptions: { [key: string]: string } = {};
        p.keys.filter(k => !mandatoryParams.includes(k))
              .forEach((k => extraOptions[k] = p.get(k) as string));

        const config: PlotConfiguration = {
          benchmark: p.get("benchmark") as string,
          commits: p.getAll("commits"),
          devices: p.getAll("devices"),
          plotType: p.get("plotType") as string,
          labelForTitle: p.get('labelForTitle') as string,
          labelForXAxis: p.get('labelForXAxis') as string,
          labelForYAxis: p.get('labelForYAxis') as string,
          options: extraOptions
        };
        this.chartTitle = config.labelForTitle;
        this.xLabel = config.labelForXAxis;
        this.yLabel = config.labelForYAxis;

        this.dataHandler.getPlotData(config).subscribe(d => this.chartData = d);
      }
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
    this.chart.refresh();
  }
}
