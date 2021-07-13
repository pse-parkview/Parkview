import { Component, OnInit } from '@angular/core';
import {PlotService} from "../../../logic/plothandler/plot.service";
import {ActivatedRoute, ParamMap} from "@angular/router";

@Component({
  selector: 'app-benchmark-comparison-plot',
  templateUrl: './benchmark-comparison-plot.component.html',
  styleUrls: ['./benchmark-comparison-plot.component.scss']
})
export class BenchmarkComparisonPlotComponent implements OnInit {

  constructor(private readonly route: ActivatedRoute,
              private readonly plotService: PlotService) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(
      (paramMap : ParamMap) => {
        if (paramMap.has('pcfg')) {
          //this.plotService.getPlotData(paramMap.get('pcfg'))
        }
      }
    )
  }
}
