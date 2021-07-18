import { Component, OnInit } from '@angular/core';
import {CommitSelectionService} from "../../../logic/commit-selection-handler/commit-selection.service";

@Component({
  selector: 'app-side-current-chosen-commit',
  templateUrl: './side-current-chosen-commit.component.html',
  styleUrls: ['./side-current-chosen-commit.component.scss']
})
export class SideCurrentChosenCommitComponent implements OnInit {

  constructor(readonly commitService: CommitSelectionService) { }

  ngOnInit(): void {
  }


}
