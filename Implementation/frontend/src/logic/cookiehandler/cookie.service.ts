import { Injectable } from '@angular/core';
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  constructor() { }

  public getRecentPlotConfigurations(): PlotConfiguration[] {
    return [];
  }
}
