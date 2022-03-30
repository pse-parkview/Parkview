import {Component, OnInit, ViewChild} from '@angular/core';
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {SelectedCommits} from "../../../logic/commit-selection-handler/interfaces/selected-commits";
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {RestService} from "../../../logic/datahandler/rest.service";
import {PlotConfiguration, SupportedChartType} from "../../../logic/plothandler/interfaces/plot-configuration";
import {
  PlotOption,
  PlotTypeOption,
  X_AXIS_PLOT_OPTION_NAME,
  Y_AXIS_PLOT_OPTION_NAME
} from "../../../logic/plothandler/interfaces/available-plot-types";
import {Router} from "@angular/router";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {MatExpansionPanel} from "@angular/material/expansion";
import {SnackBarService} from "../../../lib/notificationhandler/snack-bar.service";
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";
import {KotlinDummyDataService} from "../../../logic/datahandler/kotlin/kotlin-dummy-data.service";

@Component({
  selector: 'app-plot-configuration-dialog',
  templateUrl: './plot-configuration-dialog.component.html',
  styleUrls: ['./plot-configuration-dialog.component.scss']
})
export class PlotConfigurationDialogComponent implements OnInit {

  @ViewChild(MatExpansionPanel) customizationPanel: MatExpansionPanel = {} as MatExpansionPanel;

  benchmarkName: string = '';
  commitsAndDevices: Pair[] = [];

  availablePlotTypeOptions: PlotTypeOption[] = [];
  currentPlotTypeOption: PlotTypeOption = {
    plotName: '',
    plottableAs: [],
    options: [],
  };
  currentChartType: SupportedChartType = 'line';

  availablePlotOptions: PlotOption[] = [];
  currentPlotOptions: { [key: string]: string | number } = {};

  customizePlotLabels: boolean = false;
  plotlabelTitle: string = '';
  plotlabelXAxis: string = 'x';
  plotlabelYAxis: string = 'y';

  constructor(private readonly commitSelectService: SelectionService,
              private readonly dataService: KotlinDummyDataService,
              private readonly router: Router,
              private readonly cookieService: CookieService,
              private readonly notificationService: SnackBarService) {
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

  updatePlotTypeOption(): void {
    this.currentChartType = this.currentPlotTypeOption.plottableAs !== undefined && this.currentPlotTypeOption.plottableAs.length > 0
      ? this.currentPlotTypeOption.plottableAs[0]
      : 'line';

    this.availablePlotOptions = this.currentPlotTypeOption.options;
    this.currentPlotOptions = {};
    this.availablePlotOptions.forEach(po => {
      if (po.number) {
        this.currentPlotOptions[po.name] = !isNaN(parseFloat(po.default)) ? parseFloat(po.default) : 0;
      } else {
        this.currentPlotOptions[po.name] = po.options.includes(po.default)
          ? po.default
          : po.options.length > 0 ? po.options[0] : '';
      }
    });
    this.setDefaultLabels();
  }

  refreshDefaults() {
    if (!this.customizePlotLabels) {
      this.setDefaultLabels();
    }
  }

  saveAndStoreCurrentConfig(): void {
    this.cookieService.addTemplate(this.compilePlotConfig());
    this.notificationService.notify('Saved plot configurations within cookies');
  }

  navigateToPlotView(): void {
    const config: PlotConfiguration = this.compilePlotConfig();
    this.cookieService.addRecentPlotConfiguration(config);
    const qp = {
      ...config,
        options: null,
      ...config.options
    };
    console.log(config.options);
    this.router.navigate([config.chartType], {queryParams: qp});
  }

  validPlotConfig(): boolean {
    return PlotUtils.isValidConfig(this.compilePlotConfig());
  }

  private fetchAvailablePlots(): void {
    const commits = this.commitsAndDevices.map(p => p.commit);
    const devices = this.commitsAndDevices.map(p => p.device);
    this.dataService.getAvailablePlots(this.benchmarkName, commits, devices).subscribe((plotTypeOptions: PlotTypeOption[]) => {
      this.availablePlotTypeOptions = plotTypeOptions;
      this.currentPlotTypeOption = this.availablePlotTypeOptions.length > 0 ? this.availablePlotTypeOptions[0] : {
        plotName: '',
        plottableAs: [],
        options: []
      };
      this.updatePlotTypeOption();
    });
  }

  private compilePlotConfig(): PlotConfiguration {
    const optionsObject: { [keys: string]: string } = {};
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
      chartType: this.currentChartType
    };
  }

  private setDefaultLabels() {
    this.plotlabelTitle = this.currentPlotTypeOption.plotName;
    this.plotlabelXAxis = this.currentPlotTypeOption.options.some(o => o.name === X_AXIS_PLOT_OPTION_NAME)
      ? this.currentPlotOptions[X_AXIS_PLOT_OPTION_NAME].toString()
      : 'X';
    this.plotlabelYAxis = this.currentPlotTypeOption.options.some(o => o.name === Y_AXIS_PLOT_OPTION_NAME)
      ? this.currentPlotOptions[Y_AXIS_PLOT_OPTION_NAME].toString()
      : 'Y';
  }
}
