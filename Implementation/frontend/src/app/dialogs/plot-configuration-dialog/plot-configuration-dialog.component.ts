import { Component, OnInit } from '@angular/core';
import {CommitSelectionService} from "../../../logic/commit-selection-handler/commit-selection.service";
import {SelectedCommits} from "../../../logic/commit-selection-handler/interfaces/selected-commits";
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {DataService} from "../../../logic/datahandler/data.service";
import {
  AvailablePlotTypes,
  PlotOption,
  PlotTypeOption
} from "../../../logic/plothandler/interfaces/available-plot-types";
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

  availablePlotTypeOptions: PlotTypeOption[] = [];
  currentPlotTypeOption: PlotTypeOption = {
    plotName: '',
    options: [],
  };

  availablePlotOptions: PlotOption[] = [];
  currentPlotOptions: Map<PlotOption, string>[] = []

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
      this.availablePlots = {
        get bar(): PlotTypeOption[] {
          return [];
        }, get line(): PlotTypeOption[] {
          return [];
        }, get scatter(): PlotTypeOption[] {
          return [
            {
              plotName: 'bringe',
              options: [
                {
                  name: 'pogers',
                  options: [],
                  number: true,
                }
              ]
            }
          ];
        }, get stackedBar(): PlotTypeOption[] {
          return [];
        }

      }
      this.filterOutEmptyPlotTypeKeys();
    });
  }

  filterOutEmptyPlotTypeKeys() {
    this.resetPlotTypeKeyValues();
    this.availablePlotTypeKeys = Object.keys(this.availablePlots).filter(k => ((this.availablePlots as any)[k] as PlotTypeOption[]).length !== 0);
    this.currentPlotTypeKey = this.availablePlotTypeKeys.length !== 0 ? this.availablePlotTypeKeys[0] : '';
    this.updatePlotTypeKey();
  }

  updatePlotTypeKey() {
    this.availablePlotTypeOptions = (this.availablePlots as any)[this.currentPlotTypeKey] as PlotTypeOption[];
    this.currentPlotTypeOption = this.availablePlotTypeOptions.length !== 0 ? this.availablePlotTypeOptions[0] : { plotName: '', options: [] };
    this.updatePlotTypeOption()
  }

  updatePlotTypeOption() {
    this.availablePlotOptions = this.currentPlotTypeOption.options;
  }

  resetPlotTypeKeyValues() {
    this.availablePlotTypeKeys = [];
    this.currentPlotTypeKey = '';
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
      plotType: "",
      options: new Map<string, string>(),
    };
  }

}
