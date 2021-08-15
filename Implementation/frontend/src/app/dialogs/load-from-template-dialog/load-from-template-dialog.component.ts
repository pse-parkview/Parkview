import { Component, OnInit } from '@angular/core';
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-load-from-template-dialog',
  templateUrl: './load-from-template-dialog.component.html',
  styleUrls: ['./load-from-template-dialog.component.scss']
})
export class LoadFromTemplateDialogComponent implements OnInit {

  configs: PlotConfiguration[] = [];
  currentSelectedPlotConfiguration: PlotConfiguration = {} as PlotConfiguration;

  constructor(private readonly cookieService: CookieService,
              private readonly router: Router) { }

  ngOnInit(): void {
    this.currentSelectedPlotConfiguration = this.configs.length > 0 ? this.configs[0] : {} as PlotConfiguration;
  }

  navigateToPlotView(): void {
    const config: PlotConfiguration = this.currentSelectedPlotConfiguration;
    this.cookieService.addRecentPlotConfiguration(config);
    const qp = {
      ...config,
      options: null,
      ...config.options
    };
    this.router.navigate([config.chartType], {queryParams: qp});
  }
}
