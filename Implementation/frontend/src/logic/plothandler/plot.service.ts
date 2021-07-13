import {Injectable} from '@angular/core';
import {PlotType} from './interfaces/plot-type';
import {Data} from './interfaces/data';
import {DataService} from "../datahandler/data.service";
import {PlotConfiguration} from "./interfaces/plot-configuration";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PlotService {

  constructor(private readonly dataService: DataService) {
  }

  public requestGraphData(config: PlotConfiguration): Observable<Data[]> {

    switch (config.plotType) {

      case PlotType.LINE: // fallthrough because data has the same format
      case PlotType.BAR:
        return this.dataService.getData(config);

      case PlotType.SCATTER:
        /*
                        let res = this.http.get<Data[]>(this.URL);
                        let data: any[] = [];
                        res.subscribe((values: Data[]) => data = values);
                let data: Data[] = json;
                for (let d of data) {
                  for (let s of d.series) {
                    s.x = s.name;
                    s.y = s.value;
                    s.r = 1;
                  }
                }
                console.log(data);
                return data;
        */
        return this.dataService.getData(config); // TODO remodel data to fit scatter plot
    }
  }
}
