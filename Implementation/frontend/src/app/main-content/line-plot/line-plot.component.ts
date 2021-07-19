import {Component, OnInit} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {Observable} from "rxjs";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";

@Component({
  selector: 'app-line-plot',
  templateUrl: './line-plot.component.html',
  styleUrls: ['./line-plot.component.scss']
})
export class LinePlotComponent implements OnInit {
  public chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: true,
    legend: {display: true},
    elements: {
      line: {
        tension: 0,
      }
    },
    scales: {
      yAxes: [{
        type: 'logarithmic'
      }],
      xAxes: [{
        type: 'linear',
      }]
    },
  };

  public chartData: ChartDataSets[] = Array();
  public chartType: ChartType = 'line';
  public labels = Array();

  constructor(private readonly route: ActivatedRoute, private readonly dataHandler: DataService) {
  }

  ngOnInit() {
    this.readParams(this.route.queryParamMap);
  }

  readParams(params: Observable<ParamMap>) {
    params.subscribe(p => {
      if (p.has('benchmark') && p.has('commits') && p.has('devices') && p.has('plotType')) {
        // Get additional configuration
        let extraOptions: { [key: string]: string } = {};
        for (let param of p.keys) {
          if (param !== 'benchmark' && param !== 'commits' && param !== 'devices' && param !== 'plotType') {
            extraOptions[param] = p.get(param) as string;
          }
        }
        // Build request
        let config: PlotConfiguration = {
          benchmark: p.get("benchmark") as string,
          commits: p.getAll("commits") as string[],
          devices: p.getAll("devices") as string[],
          plotType: p.get("plotType") as string,
          options: extraOptions
        };

        // Make request and read data
        this.dataHandler.getPlotData(config).subscribe(d => this.chartData = d);
      }
    });
  }
}
