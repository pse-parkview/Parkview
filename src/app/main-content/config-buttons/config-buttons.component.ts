import {Component, OnInit} from '@angular/core';
import {PlotUtils} from "../../../lib/plot-component-util/plot-utils";

@Component({
  selector: 'app-config-buttons',
  templateUrl: './config-buttons.component.html',
  styleUrls: ['../../../lib/plot-component-util/styles/plot-component-styles.scss']
})
export class ConfigButtonsComponent implements OnInit {

  public url = window.location.href;

  constructor() {
  }

  ngOnInit(): void {
  }

  downloadCanvas(event: MouseEvent) {
    PlotUtils.downloadCanvas(event);
  }
}
