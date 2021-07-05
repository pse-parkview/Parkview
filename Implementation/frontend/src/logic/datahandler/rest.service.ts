import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {Data} from "../plothandler/interfaces/data";
import {PlotType} from "../plothandler/interfaces/plot-type";

@Injectable({
  providedIn: 'root'
})
export class RestService {

  private URL: string = "";

  constructor(private http: HttpClient) {
  }

  set withURL(URL: string) {
    this.URL = URL;
  }

  private requestGraphData(type: PlotType) {
    switch (type) {

      case PlotType.LINE: // fallthrough because data has the same format
      case PlotType.BAR:
        return this.http.get<Data[]>(this.URL);

      case PlotType.SCATTER:
        let res = this.http.get<Data[]>(this.URL);
        let data: any[] = [];
        res.subscribe((values: Data[]) => data = values);
        for (let d of data) {
          d.series.x = d.series.name;
          d.series.y = d.series.value;
          d.series.r = 1;
        }
        return data;
    }
  }
}
