import {Component, OnInit} from '@angular/core';
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {Router} from "@angular/router";

/**
 * It appears as if this component doesn't actually need PlotCardCompenent
 */
@Component({
  selector: 'app-side-previous-plots',
  templateUrl: './side-previous-plots.component.html',
  styleUrls: ['./side-previous-plots.component.scss']
})
export class SidePreviousPlotsComponent implements OnInit {

  recentPlotConfigurations: PlotConfiguration[] = [];

  constructor(private readonly cookieService: CookieService,
              private readonly router: Router) {
  }

  ngOnInit(): void {
    this.recentPlotConfigurations = this.cookieService.getRecentPlotConfigurations();
  }

  navigateToPlotView(config: PlotConfiguration) {
    // this.router.navigate(['singleBenchmark_plot_or_so'], {queryParams: {pc: compilePlotConfig()}});
    // and let them do the work or something
    const qp = {
      ...config,
      options: null,
      ...config.options
    };
    this.router.navigate([config.chartType], {queryParams: qp});
  }

}
