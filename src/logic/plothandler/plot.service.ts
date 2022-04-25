import {Injectable} from '@angular/core';
import {ParkviewLibDataService} from "../datahandler/kotlin/parkview-lib-data.service";

@Injectable({
  providedIn: 'root'
})
export class PlotService {

  constructor(private readonly dataService: ParkviewLibDataService) {
  }

  //TODO getGraphData should be implemented correctly later.
  //TODO instead of using the Data interface, we should probably use chart.js' ChartDataSet instead

  formatCommits(commits: string[]) {
    let res = "";
    let first = true;
    for (let commit of commits) {
      if (!first) {
        res += ", ";
      }
      first = false;
      res += commit.substr(0, 7);
    }
    return res;
  }

  formatDevices(devices: string[]) {
    let res = "";
    let first = true;
    // Convert to set and back to Array to remove duplicates
    for (let device of [...new Set(devices)]) {
      if (!first) {
        res += ", ";
      }
      first = false;
      res += device;
    }
    return res;
  }
}
