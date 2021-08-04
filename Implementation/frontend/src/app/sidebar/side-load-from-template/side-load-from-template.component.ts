import { Component, OnInit } from '@angular/core';
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {Observer} from "rxjs";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotTypeOption} from "../../../logic/plothandler/interfaces/available-plot-types";

@Component({
  selector: 'app-side-load-from-template',
  templateUrl: './side-load-from-template.component.html',
  styleUrls: ['./side-load-from-template.component.scss']
})
export class SideLoadFromTemplateComponent implements OnInit {

  usableTemplates: PlotConfiguration[] = [];

  private readonly templateViewUpdater: Observer<void> = {
    next: () => {
      const allTemplates = this.cookieService.getSavedTemplates();
      const selectedCommits = this.commitSelectionService.getSelectedCommits();
      const commits = selectedCommits.commitsAndDevices.map(p => p.commit);
      const devices = selectedCommits.commitsAndDevices.map(p => p.device);
      this.dataService.getAvailablePlots(selectedCommits.benchmarkName, commits, devices).subscribe((plotTypeOptions: PlotTypeOption[]) => {
        this.usableTemplates = allTemplates.filter(plotConfig => plotTypeOptions.some(pto => pto.plotName == plotConfig.plotType));
      })
    },
    error: (err) => {
      console.error(err);
    },
    complete: () => {
      // do nothing
    },
  }

  constructor(private readonly commitSelectionService: SelectionService,
              private readonly cookieService: CookieService,
              private readonly dataService: DataService) {
    // only suitable for DI
  }

  ngOnInit(): void {
    this.cookieService.savedTemplateUpdate.subscribe(this.templateViewUpdater);
    this.commitSelectionService.selectedCommitsHasUpdated.subscribe(this.templateViewUpdater);
  }
}
