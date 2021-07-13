import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Data} from "../plothandler/interfaces/data";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";

@Injectable({
  providedIn: 'root'
})
export class RestService {

  constructor(private http: HttpClient) {
  }


  public makeRequest(config: PlotConfiguration, url: string) {

    // use config to make url

    return this.http.get<Data[]>(url);
  }
}
