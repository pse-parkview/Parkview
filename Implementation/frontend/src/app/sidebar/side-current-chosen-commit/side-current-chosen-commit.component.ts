import {Component, OnInit} from '@angular/core';
import {SelectionService} from "../../../logic/commit-selection-handler/selection.service";
import {MatDialog} from "@angular/material/dialog";
import {PlotConfigurationDialogComponent} from "../../dialogs/plot-configuration-dialog/plot-configuration-dialog.component";
import {SnackBarService} from "../../../lib/notificationhandler/snack-bar.service";

@Component({
  selector: 'app-side-current-chosen-commit',
  templateUrl: './side-current-chosen-commit.component.html',
  styleUrls: ['./side-current-chosen-commit.component.scss']
})
export class SideCurrentChosenCommitComponent implements OnInit {

  constructor(readonly commitService: SelectionService,
              private readonly dialog: MatDialog,
              private readonly snackBarService: SnackBarService) {
  }

  ngOnInit(): void {
  }

  configurePlot() {
    const selectedCommits = this.commitService.getSelectedCommits();
    if (selectedCommits.benchmarkName.trim() === '') {
      this.snackBarService.notify('You didnt select any benchmark yet.');
      return;
    }
    if (selectedCommits.commitsAndDevices.length === 0) {
      this.snackBarService.notify('You didnt select any commits and devices to compare yet.');
      return;
    }
    this.dialog.open(PlotConfigurationDialogComponent);
  }
}
