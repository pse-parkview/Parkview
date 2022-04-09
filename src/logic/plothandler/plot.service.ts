import {Injectable} from '@angular/core';
import {RestService} from "../datahandler/rest.service";
import {ParkviewLibDataService} from "../datahandler/kotlin/parkview-lib-data.service";

@Injectable({
  providedIn: 'root'
})
export class PlotService {

  constructor(private readonly dataService: ParkviewLibDataService) {
  }

  //TODO getGraphData should be implemented correctly later.
  //TODO instead of using the Data interface, we should probably use chart.js' ChartDataSet instead
}
