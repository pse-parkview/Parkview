import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-line-plot',
  templateUrl: './line-plot.component.html',
  styleUrls: ['./line-plot.component.scss']
})
export class LinePlotComponent implements OnInit {

  ngOnInit(): void {
  }
  view: [number, number] = [700, 300];

  // options
  legend: boolean = true;
  showLabels: boolean = true;
  animations: boolean = true;
  xAxis: boolean = true;
  yAxis: boolean = true;
  showYAxisLabel: boolean = true;
  showXAxisLabel: boolean = true;
  xAxisLabel: string = 'Year';
  yAxisLabel: string = 'Population';
  timeline: boolean = true;
  scheme = 'cool';

  constructor() {
  }

  onSelect(data: any): void {
    console.log('Item clicked', JSON.parse(JSON.stringify(data)));
  }

  onActivate(data: any): void {
    console.log('Activate', JSON.parse(JSON.stringify(data)));
  }

  onDeactivate(data: any): void {
    console.log('Deactivate', JSON.parse(JSON.stringify(data)));
  }

  /*
  let gb-bg = '#fbf1c7';
  let gb-light-gray = '#928374';
  let gb-red = '#cc241d;
  let gb-dark-red = '#9d0006;
  let gb-green = '#98971a;
  let gb-dark-green = '#79740e;
  let gb-yellow = '#d79921;
  let gb-dark-yellow = '#b57614;
  let gb-blue = '#458588;
  let gb-dark-blue = '#076678;
  let gb-purple = '#b16286;
  let gb-dark-purple = '#8f3f71;
  let gb-aqua  = '#689d6a;
  let gb-dark-aqua  = '#427b58;
  let gb-gray  = '#7c6f64;
  let gb-fg  = '#3c3836;
  let gb-orange  = '#d65d0e;
  let gb-dark-orange  = '#af3a03;
   */

  multi: any[] = [
    {
      "name": "Germany",
      "series": [
        {
          "name": "1990",
          "value": 62000000
        },
        {
          "name": "2010",
          "value": 73000000
        },
        {
          "name": "2011",
          "value": 89400000
        }
      ]
    },

    {
      "name": "USA",
      "series": [
        {
          "name": "1990",
          "value": 250000000
        },
        {
          "name": "2010",
          "value": 309000000
        },
        {
          "name": "2011",
          "value": 311000000
        }
      ]
    },

    {
      "name": "France",
      "series": [
        {
          "name": "1990",
          "value": 58000000
        },
        {
          "name": "2010",
          "value": 50000020
        },
        {
          "name": "2011",
          "value": 58000000
        }
      ]
    },
    {
      "name": "UK",
      "series": [
        {
          "name": "1990",
          "value": 57000000
        },
        {
          "name": "2010",
          "value": 62000000
        }
      ]
    }
  ];


}
