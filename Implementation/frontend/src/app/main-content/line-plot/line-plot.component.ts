import {Component, OnInit} from '@angular/core';
import {ChartData, ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {ActivatedRoute, Params} from "@angular/router";
import {Observable} from "rxjs";
import lineData from './line.json'; // Temporary test data TODO: remove

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

  constructor(private readonly route: ActivatedRoute) {
  }


  ngOnInit() {
    this.chartData = lineData;
    // this.readParams(this.route.queryParams);
    // this.chartData = this.getData();
  }



  readParams(params: Observable<Params>) {
    // TODO: parse query params here
  }

  getData() {
    // TODO: get data from data handler here
    return lineData;
  }
}
