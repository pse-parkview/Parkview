import { Component, OnInit } from '@angular/core';
import {CommitSelectionService} from "../../../logic/commit-selection-handler/commit-selection.service";
import {SelectedCommits} from "../../../logic/commit-selection-handler/interfaces/selected-commits";
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {DataService} from "../../../logic/datahandler/data.service";
import {AvailablePlotTypes, NameWithAvailableXAxis} from "../../../logic/plothandler/interfaces/available-plot-types";
import {PlotConfiguration} from "../../../logic/plothandler/interfaces/plot-configuration";

@Component({
  selector: 'app-plot-configuration-dialog',
  templateUrl: './plot-configuration-dialog.component.html',
  styleUrls: ['./plot-configuration-dialog.component.scss']
})
export class PlotConfigurationDialogComponent implements OnInit {

  benchmarkName: string = '';
  commitsAndDevices: Pair[] = [];

  availablePlots: AvailablePlotTypes = {
    bar: [],
    line: [],
    scatter: [],
    stackedBar: [],
  }
  availablePlotTypeKeys: string[] = [];
  currentPlotTypeKey: string = '';

  availablePlotNames: string[] = [];
  currentPlotName: string = '';
  availableXAxis: string[] = [];
  currentxAxis: string = '';

  constructor(private readonly commitSelectService: CommitSelectionService,
              private readonly dataService: DataService) {
  }

  ngOnInit(): void {
    const commitSelection: SelectedCommits = this.commitSelectService.getSelectedCommits();
    this.benchmarkName = commitSelection.benchmarkName;
    this.commitsAndDevices = commitSelection.commitsAndDevices;
    this.fetchAvailablePlots();
  }

  fetchAvailablePlots() {
    const commits = this.commitsAndDevices.map(p => p.commit);
    const devices = this.commitsAndDevices.map(p => p.device);
    this.dataService.getAvailablePlots(this.benchmarkName, commits, devices).subscribe((availablePlots: AvailablePlotTypes) => {
      this.availablePlots = availablePlots;
      this.filterOutEmptyPlotTypeKeys();
    });
  }

  filterOutEmptyPlotTypeKeys() {
    this.resetPlotTypeKeyValues();
    this.availablePlotTypeKeys = Object.keys(this.availablePlots).filter(k => ((this.availablePlots as any)[k] as NameWithAvailableXAxis[]).length !== 0);
    this.currentPlotTypeKey = this.availablePlotTypeKeys.length !== 0 ? this.availablePlotTypeKeys[0] : '';
    this.selectPlotTypeKey();
  }

  selectPlotTypeKey() {
    this.resetPlotNameValues();
    this.availablePlotNames = ((this.availablePlots as any)[this.currentPlotTypeKey] as NameWithAvailableXAxis[]).map(n => n.name);
    this.currentPlotName = this.availablePlotNames.length !== 0 ? this.availablePlotNames[0] : '';
    this.selectPlotName()
  }

  selectPlotName() {
    this.resetXAxisValues();
    const nameWithXAxis = ((this.availablePlots as any)[this.currentPlotTypeKey] as NameWithAvailableXAxis[]).find(n => n.name === this.currentPlotName);
    this.availableXAxis = nameWithXAxis !== undefined ? nameWithXAxis.xAxis : [];
    this.currentxAxis = this.availableXAxis.length !== 0 ? this.availableXAxis[0] : '';
  }

  resetPlotTypeKeyValues() {
    this.availablePlotTypeKeys = [];
    this.currentPlotTypeKey = '';
  }

  resetPlotNameValues() {
    this.availablePlotNames = [];
    this.currentPlotName = '';
  }

  resetXAxisValues() {
    this.availableXAxis = [];
    this.currentxAxis = '';
  }

  saveAndStoreCurrentConfig() {
    // this.cookieService.store(compilePlotConfig()); oder so
  }

  navigateToPlotView() {
    // this.router.navigate(['singleBenchmark_plot_or_so'], {queryParams: {pc: compilePlotConfig()}});
    // and let them do the work or something
  }

  compilePlotConfig(): PlotConfiguration {
    return {
      benchmark: this.benchmarkName,
      commits: this.commitsAndDevices.map(p => p.commit),
      devices: this.commitsAndDevices.map(p => p.device),
      plotType: this.currentPlotName,
      xAxis: this.currentxAxis,
    };
  }

}
