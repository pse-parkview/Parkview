import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-plot-configuration-dialog',
  templateUrl: './plot-configuration-dialog.component.html',
  styleUrls: ['./plot-configuration-dialog.component.scss']
})
export class PlotConfigurationDialogComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}

enum PLotTypes {
  BAR_PLOT = "Bar Plot",
  LINE_PLOT = "Line Plot",
  SCATTER_PLOT = "Scatter Plot"
}
