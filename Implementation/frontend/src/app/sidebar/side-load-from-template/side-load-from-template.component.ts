import { Component, OnInit } from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {LoadFromTemplateDialogComponent} from "../../dialogs/load-from-template-dialog/load-from-template-dialog.component";

@Component({
  selector: 'app-side-load-from-template',
  templateUrl: './side-load-from-template.component.html',
  styleUrls: ['./side-load-from-template.component.scss']
})
export class SideLoadFromTemplateComponent implements OnInit {

  constructor(private readonly matDialog: MatDialog) {
    // only suitable for DI
  }

  ngOnInit(): void {
    // no initialiation necessary
  }

  spawnTemplateDialog(): void {
    this.matDialog.open(LoadFromTemplateDialogComponent);
  }
}
