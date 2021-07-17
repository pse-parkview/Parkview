import { Component, OnInit } from '@angular/core';
import {CommitService} from "../../../logic/commithandler/commit.service";

@Component({
  selector: 'app-side-current-chosen-commit',
  templateUrl: './side-current-chosen-commit.component.html',
  styleUrls: ['./side-current-chosen-commit.component.scss']
})
export class SideCurrentChosenCommitComponent implements OnInit {

  constructor(readonly commitService: CommitService) { }

  ngOnInit(): void {
  }


}
