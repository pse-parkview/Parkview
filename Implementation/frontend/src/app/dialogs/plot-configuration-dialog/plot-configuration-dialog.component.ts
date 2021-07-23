import {Component, OnInit, ViewChild} from '@angular/core';
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {SelectedCommits} from "../../../logic/commit-selection-handler/interfaces/selected-commits";
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {DataService} from "../../../logic/datahandler/data.service";
import {PlotConfiguration, SupportedChartType} from "../../../logic/plothandler/interfaces/plot-configuration";
import {AvailablePlotTypes, PlotOption, PlotTypeOption, X_AXIS_PLOT_OPTION_NAME, Y_AXIS_PLOT_OPTION_NAME} from "../../../logic/plothandler/interfaces/available-plot-types";
import {Router} from "@angular/router";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {MatExpansionPanel} from "@angular/material/expansion";

@Component({
  selector: 'app-plot-configuration-dialog',
  templateUrl: './plot-configuration-dialog.component.html',
  styleUrls: ['./plot-configuration-dialog.component.scss']
})
export class PlotConfigurationDialogComponent implements OnInit {

  @ViewChild(MatExpansionPanel) customizationPanel: MatExpansionPanel = {} as MatExpansionPanel;

  benchmarkName: string = '';
  commitsAndDevices: Pair[] = [];

  availablePlots: AvailablePlotTypes = {
    bar: [],
    line: [],
    scatter: [],
    stackedBar: [],
  }
  availablePlotTypeKeys: SupportedChartType[] = [];
  currentPlotTypeKey: SupportedChartType = 'line';

  availablePlotTypeOptions: PlotTypeOption[] = [];
  currentPlotTypeOption: PlotTypeOption = {
    plotName: '',
    options: [],
  };

  availablePlotOptions: PlotOption[] = [];
  currentPlotOptions: { [key: string]: string | number } = {};

  customizePlotLabels: boolean = false;
  plotlabelTitle: string = '';
  plotlabelXAxis: string = 'x';
  plotlabelYAxis: string = 'y';

  constructor(private readonly commitSelectService: SelectionService,
              private readonly dataService: DataService,
              private readonly router: Router,
              private readonly recent: CookieService) {
  }

  ngOnInit(): void {
    const commitSelection: SelectedCommits = this.commitSelectService.getSelectedCommits();
    this.benchmarkName = commitSelection.benchmarkName;
    this.commitsAndDevices = commitSelection.commitsAndDevices;
    this.fetchAvailablePlots();
  }

  togglePanel(): void {
    this.customizationPanel.toggle();
    if (!this.customizePlotLabels) {
      this.setDefaultLabels();
    }
  }

  filterOutEmptyPlotTypeKeys(): void {
    this.resetPlotTypeKeyValues();
    this.availablePlotTypeKeys = Object.keys(this.availablePlots).filter(k => ((this.availablePlots as any)[k] as PlotTypeOption[]).length !== 0) as SupportedChartType[];
    this.currentPlotTypeKey = this.availablePlotTypeKeys.length !== 0 ? this.availablePlotTypeKeys[0] : 'line';
    this.updatePlotTypeKey();
  }

  updatePlotTypeKey(): void {
    this.availablePlotTypeOptions = (this.availablePlots as any)[this.currentPlotTypeKey] as PlotTypeOption[];
    this.currentPlotTypeOption = this.availablePlotTypeOptions.length !== 0 ? this.availablePlotTypeOptions[0] : { plotName: '', options: [] };
    this.updatePlotTypeOption()
  }

  updatePlotTypeOption(): void {
    this.availablePlotOptions = this.currentPlotTypeOption.options;
    this.currentPlotOptions = {};
    this.availablePlotOptions.forEach(po => {
      if (po.number) {
        this.currentPlotOptions[po.name] = 0;
      } else {
        this.currentPlotOptions[po.name] = po.options.length > 0 ? po.options[0] : ''
      }
    });
    this.setDefaultLabels();
  }

  private fetchAvailablePlots(): void {
    const commits = this.commitsAndDevices.map(p => p.commit);
    const devices = this.commitsAndDevices.map(p => p.device);
    this.dataService.getAvailablePlots(this.benchmarkName, commits, devices).subscribe((availablePlots: AvailablePlotTypes) => {
      this.availablePlots = availablePlots;
      this.filterOutEmptyPlotTypeKeys();
    });
  }

  private resetPlotTypeKeyValues(): void {
    this.availablePlotTypeKeys = [];
    this.currentPlotTypeKey = 'line';
  }

  saveAndStoreCurrentConfig(): void {
    // this.cookieService.store(compilePlotConfig()); oder so
    alert(`todo: store ${JSON.stringify(this.compilePlotConfig())} as a cookie or similar`);
    console.log(this.compilePlotConfig());

  }

  navigateToPlotView(): void {
    // this.router.navigate(['singleBenchmark_plot_or_so'], {queryParams: {pc: compilePlotConfig()}});
    // and let them do the work or something
    const config: PlotConfiguration = this.compilePlotConfig();
    this.recent.addRecentPlotConfiguration(config);
    const qp = {
      ...config,
      options: null,
      ...config.options
    };
    this.router.navigate([this.currentPlotTypeKey], {queryParams: qp});
  }

  private compilePlotConfig(): PlotConfiguration {
    const optionsObject: {[keys: string]: string}= {};
    this.currentPlotTypeOption.options.forEach(o => {
      optionsObject[o.name] = this.currentPlotOptions[o.name].toString();
    });
    return {
      benchmark: this.benchmarkName,
      commits: this.commitsAndDevices.map(p => p.commit.sha),
      devices: this.commitsAndDevices.map(p => p.device),
      plotType: this.currentPlotTypeOption.plotName,
      labelForTitle: this.plotlabelTitle,
      labelForXAxis: this.plotlabelXAxis,
      labelForYAxis: this.plotlabelYAxis,
      options: optionsObject,
      chartType: this.currentPlotTypeKey
    };
  }

  refreshDefaults() {
    if (!this.customizePlotLabels) {
      this.setDefaultLabels();
    }
  }

  private setDefaultLabels() {
    this.plotlabelTitle = this.currentPlotTypeOption.plotName
    this.plotlabelXAxis = this.currentPlotTypeOption.options.some(o => o.name === X_AXIS_PLOT_OPTION_NAME)
      ? this.currentPlotOptions[X_AXIS_PLOT_OPTION_NAME].toString()
      : 'X';
    this.plotlabelYAxis = this.currentPlotTypeOption.options.some(o => o.name === Y_AXIS_PLOT_OPTION_NAME)
      ? this.currentPlotOptions[Y_AXIS_PLOT_OPTION_NAME].toString()
      : 'Y';
  }
}
