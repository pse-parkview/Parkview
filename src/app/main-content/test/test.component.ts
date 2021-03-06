import {Component, OnInit} from '@angular/core';
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {MatDialog} from "@angular/material/dialog";
import {PlotConfigurationDialogComponent} from "../../dialogs/plot-configuration-dialog/plot-configuration-dialog.component";
import {SummaryConfig} from "../../../lib/directives/summary-card/summary-card.component";
import {ParkviewLibDataService} from "../../../logic/datahandler/kotlin/parkview-lib-data.service";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit { // TODO: delete this component

  readonly testSummaryConfig: SummaryConfig = {
    benchmarkName: 'Spmv',
    commitSha: 'c101790ff69e4dd285bf60ac29ee745d8bc306b8',
    device: 'Xeon_Gold_6230-solver',
  };

  constructor(private readonly dataService: ParkviewLibDataService,
              private readonly commitService: SelectionService,
              private readonly matDialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  tmpOnClick(event: MouseEvent) {
    const pairs: Pair[] = this.commitService.getSelectedCommits().commitsAndDevices;
    if (pairs.length === 0) {
      return;
    }
    this.dataService.getAvailablePlots('Spmv', pairs.map(p => p.commit), pairs.map(p => p.device)).subscribe((availablePlots) => {
      console.log(availablePlots);
    });

    console.log(this.commitService.getSelectedCommits().commitsAndDevices);
  }

  spawnPlotConfigDialog(event: MouseEvent) {
    this.matDialog.open(PlotConfigurationDialogComponent);
  }

}
