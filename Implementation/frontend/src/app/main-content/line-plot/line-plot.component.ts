import { Component, OnInit } from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {ActivatedRoute, Params} from "@angular/router";
import {Observable} from "rxjs";

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
  };

  public chartData: ChartDataSets[] = Array();
  public chartType: ChartType = 'line';

  constructor(private readonly route: ActivatedRoute) {
  }


  ngOnInit() {
    this.chartData = this.getData();
    this.readParams(this.route.queryParams);
    // this.chartLabels= labels;
  }

  readParams(params: Observable<Params>)  {
    // params.subscribe(p => {
    //   this.chartType = p.chartType;
    // })
  }

  getData() {
    return data;
  }
}

const data = [
  {
    label: 'poggers',
    data: [
      {
        x: 1,
        y: 2
      },
      {
        x: 2,
        y: 7
      },
      {
        x: 3,
        y: 3
      },
      {
        x: 4,
        y: 9
      },
      {
        x: 5,
        y: 2
      },
      {
        x: 6,
        y: 1
      },
      {
        x: 7,
        y: 8
      },
    ]
  },
  {
    label: 'poggus',
    data: [
      {
        x: 1,
        y: 3
      },
      {
        x: 2,
        y: 7
      },
      {
        x: 3,
        y: 1
      },
      {
        x: 4,
        y: 2
      },
      {
        x: 5,
        y: 5
      },
      {
        x: 6,
        y: 9
      },
      {
        x: 7,
        y: 2
      },
    ]
  },
  {
    label: 'pogga',
    data: [
      {
        x: 1,
        y: 6
      },
      {
        x: 2,
        y: 3
      },
      {
        x: 3,
        y: 9
      },
      {
        x: 4,
        y: 8
      },
      {
        x: 5,
        y: 9
      },
      {
        x: 6,
        y: 7
      },
      {
        x: 7,
        y: 8
      },
    ]
  },
];


