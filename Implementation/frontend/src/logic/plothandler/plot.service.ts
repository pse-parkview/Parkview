import {Injectable} from '@angular/core';
import {RestService} from "../datahandler/rest.service";

@Injectable({
  providedIn: 'root'
})
export class PlotService {

  constructor(private readonly dataService: RestService) {
  }

  //TODO getGraphData should be implemented correctly later.
  //TODO instead of using the Data interface, we should probably use chart.js' ChartDataSet instead
}
