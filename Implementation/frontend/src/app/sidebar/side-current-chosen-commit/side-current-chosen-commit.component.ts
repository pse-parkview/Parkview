import { Component, OnInit } from '@angular/core';
import {CommitSelectionService} from "../../../logic/commit-selection-handler/commit-selection.service";
import {MatDialog} from "@angular/material/dialog";
import {PlotConfigurationDialogComponent} from "../../dialogs/plot-configuration-dialog/plot-configuration-dialog.component";

@Component({
  selector: 'app-side-current-chosen-commit',
  templateUrl: './side-current-chosen-commit.component.html',
  styleUrls: ['./side-current-chosen-commit.component.scss']
})
export class SideCurrentChosenCommitComponent implements OnInit {

  constructor(readonly commitService: CommitSelectionService,
              private readonly dialog: MatDialog) { }

  ngOnInit(): void {
  }

  configurePlot() {
    this.dialog.open(PlotConfigurationDialogComponent)
  }
}
