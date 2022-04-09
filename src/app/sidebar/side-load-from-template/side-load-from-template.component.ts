import {Component, OnInit} from '@angular/core';
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {Observer} from "rxjs";
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {RestService} from "../../../logic/datahandler/rest.service";
import {PlotTypeOption} from "../../../logic/plothandler/interfaces/available-plot-types";
import {Template} from "../../../logic/cookiehandler/interfaces/template";
import {SelectedCommits} from "../../../logic/commit-selection-handler/interfaces/selected-commits";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {Router} from "@angular/router";
import {ParkviewLibDataService} from "../../../logic/datahandler/kotlin/parkview-lib-data.service";

@Component({
  selector: 'app-side-load-from-template',
  templateUrl: './side-load-from-template.component.html',
  styleUrls: ['./side-load-from-template.component.scss']
})
export class SideLoadFromTemplateComponent implements OnInit {

  usableTemplates: Template[] = [];

  private selectedCommits: SelectedCommits = {
    benchmarkName: '',
    commitsAndDevices: []
  };

  private readonly templateViewUpdater: Observer<void> = {
    next: () => {
      const allTemplates = this.cookieService.getSavedTemplates();
      this.selectedCommits = this.commitSelectionService.getSelectedCommits();
      if (this.selectedCommits.commitsAndDevices.length == 0) {
        this.usableTemplates = [];
        return;
      }
      this.dataService.getAvailablePlots(
        this.selectedCommits.benchmarkName,
        this.selectedCommits.commitsAndDevices.map(p => p.commit),
        this.selectedCommits.commitsAndDevices.map(p => p.device),
      ).subscribe((plotTypeOptions: PlotTypeOption[]) => {
        this.usableTemplates = allTemplates.filter(
          template => plotTypeOptions.some(
            pto => pto.plotName == template.config.plotType
          )
        );
      });
    },
    error: (err) => {
      console.error(err);
    },
    complete: () => {
      // do nothing
    },
  };

  constructor(private readonly commitSelectionService: SelectionService,
              private readonly cookieService: CookieService,
              private readonly dataService: ParkviewLibDataService,
              private readonly router: Router) {
    // only suitable for DI
  }

  ngOnInit(): void {
    this.cookieService.savedTemplateUpdate.subscribe(this.templateViewUpdater);
    this.commitSelectionService.selectedCommitsHasUpdated.subscribe(this.templateViewUpdater);
  }

  navigateWithTemplate(template: Template) {
    const config: PlotConfiguration = template.config;
    this.cookieService.addRecentPlotConfiguration(config);
    const qp = {
      ...config,
      shas: this.selectedCommits.commitsAndDevices.map(p => p.commit),
      devices: this.selectedCommits.commitsAndDevices.map(p => p.device),
      options: null,
      ...config.options
    };
    this.router.navigate([config.chartType], {queryParams: qp});
  }

  deleteTemplate(template: Template) {
    this.cookieService.deleteTemplate(template);
  }

  // Helper method for getting the keys out of template.config.options
  objectKeys(object: Object) {
    return Object.keys(object);
  }
}
