import {Component, OnInit} from '@angular/core';
import {ChartOptions, ChartType} from "chart.js";
import {RestService} from "../../../../logic/datahandler/rest.service";
import {SelectionService} from "../../../../logic/commit-selection-handler/selection.service";
import {Observer} from "rxjs";
import {SnackBarService} from "../../../../lib/notificationhandler/snack-bar.service";
import {Device} from "../../../../logic/datahandler/interfaces/device";
import {PlotData} from "../../../../logic/plothandler/interfaces/plot-data";
import {PlotUtils} from "../../../../lib/plot-component-util/plot-utils";
import {ParkviewLibDataService} from "../../../../logic/datahandler/kotlin/parkview-lib-data.service";

@Component({
  selector: 'app-average-performance',
  templateUrl: './average-performance.component.html',
  styleUrls: ['./average-performance.component.scss']
})
export class AveragePerformanceComponent implements OnInit {

  public chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    elements: {
      point: {
        radius: 5,
      },
      line: {
        borderWidth: 3,
        fill: false
      }
    },
    scales: {
      xAxes: [{
        type: 'time',
        time: {
          unit: 'day',
        },
      }]
    },
    legend: {display: true},
  };

  public chartData = Array();
  public chartType: ChartType = 'line';
  public chartLabels = Array();

  private currentBenchmarkType: string = '';
  private currentBranch: string = '';
  availableDevices: string[] = ['y1o', 'yo2', 'yo3'];
  currentDevice: string = '';

  private readonly onSelectionUpdate: Observer<void> = {
    complete: () => {
      // nothing to do
    },
    error: err => {
      this.notificationService.notify(`Something went terribly wrong: ${err}`);
    },
    next: () => {
      this.currentBenchmarkType = this.commitSelectionService.getSelectedCommits().benchmarkName;
      this.currentBranch = this.commitSelectionService.getSelectedBranch();
      this.updateSelections();
    }
  };

  constructor(private readonly commitSelectionService: SelectionService,
              private readonly dataService: ParkviewLibDataService,
              private readonly notificationService: SnackBarService) {
  }

  update() {
    this.dataService.getAveragePerformance(
      this.currentBenchmarkType,
      this.currentBranch,
      this.currentDevice
    ).subscribe((plotData: PlotData) => {
      this.chartData = PlotUtils.colorizeDataSet(plotData.datasets);
      this.chartLabels = plotData.labels;
    });
  }

  ngOnInit() {
    this.chartType = 'line';
    this.commitSelectionService.selectedBranchHasUpdated.subscribe(this.onSelectionUpdate);
    this.commitSelectionService.selectedCommitsHasUpdated.subscribe(this.onSelectionUpdate);
  }

  private updateSelections(): void {
    if (this.currentBenchmarkType.trim() === '' || this.currentBranch.trim() === '') {
      return;
    }
    this.dataService.getAvailableDevicesForAveragePerformance(this.currentBenchmarkType, this.currentBranch).subscribe((devices: Device[]) => {
      this.availableDevices = devices.map(d => d.name);
      this.currentDevice = this.availableDevices.length > 0 ? this.availableDevices[0] : '';
      this.update();
    });

  }
}
