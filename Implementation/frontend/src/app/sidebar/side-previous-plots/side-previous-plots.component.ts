import { Component, OnInit } from '@angular/core';
import { CookieService } from "../../../logic/cookiehandler/cookie.service";
import { PlotConfiguration } from "../../../logic/plothandler/interfaces/plot-configuration";

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

  constructor(private readonly cookieService: CookieService) { }

  ngOnInit(): void {
    this.recentPlotConfigurations = this.cookieService.getRecentPlotConfigurations();
  }

}
