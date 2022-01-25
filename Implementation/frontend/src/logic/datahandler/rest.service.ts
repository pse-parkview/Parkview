import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Summary} from "./interfaces/summary";
import {PlotData} from "../plothandler/interfaces/plot-data";
import {Device} from "./interfaces/device";
import {PlotTypeOption} from "../plothandler/interfaces/available-plot-types";
import {DataHandler} from "./interfaces/data-handler";

@Injectable({
  providedIn: 'root'
})
export class RestService implements DataHandler {
  private readonly url: string = "/backend";

  constructor(private readonly http: HttpClient) {
  }

  getBranchNames(): Observable<string[]> {
    return this.http.get<Array<string>>(this.url + '/branches');
  }

  getBenchmarks() {
    return this.http.get<Array<string>>(`${this.url}/benchmarks`);
  }

  getNumPages(branchName: string) {
    const params: HttpParams = new HttpParams()
      .set('branch', branchName);
    return this.http.get<number>(`${this.url}/numberPages`, {params: params});
  }

  getCommitHistory(branchName: string, benchmarkType: string, page: number = 1): Observable<Commit[]> {
    const params: HttpParams = new HttpParams()
      .set('branch', branchName)
      .set('page', page.toString(10))
      .set('benchmark', benchmarkType);
    return this.http.get<Array<Commit>>(`${this.url}/history`, {params: params});
  }

  getAvailablePlots(benchmarkType: string, commits: Commit[], devices: string[]): Observable<PlotTypeOption[]> {
    let params: HttpParams = new HttpParams()
      .set('benchmark', benchmarkType);
    commits.forEach(c => params = params.append('shas', c.sha));
    devices.forEach(d => params = params.append('devices', d));

    return this.http.get<Array<PlotTypeOption>>(`${this.url}/availablePlots`, {params: params});
  }

  getPlotData(config: PlotConfiguration): Observable<PlotData> {
    let params: HttpParams = new HttpParams()
      .set('benchmark', config.benchmark)
      .set('plotType', config.plotType);
    config.commits.forEach(sha => params = params.append('shas', sha));
    config.devices.forEach(d => params = params.append('devices', d));
    Object.keys(config.options).forEach(k => params = params.append(k, config.options[k]));
    return this.http.get<PlotData>(`${this.url}/plot`, {params: params});
  }

  getSummary(benchmarkType: string, commitSha: string, device: string): Observable<Summary> {
    const params: HttpParams = new HttpParams()
      .set('benchmark', benchmarkType)
      .set('sha', commitSha)
      .set('device', device);
    return this.http.get<Summary>(`${this.url}/summaryValues`, {params: params});
  }

  getAvailableDevicesForAveragePerformance(benchmarkType: string, branchName: string): Observable<Device[]> {
    const params: HttpParams = new HttpParams()
      .set('benchmark', benchmarkType)
      .set('branch', branchName);
    return this.http.get<Array<Device>>(`${this.url}/availableDevices`, {params: params});
  }

  getAveragePerformance(benchmarkType: string, branchName: string, device: string): Observable<PlotData> {
    const params: HttpParams = new HttpParams()
      .set('benchmark', benchmarkType)
      .set('branch', branchName)
      .set('device', device);
    return this.http.get<PlotData>(`${this.url}/averagePerformance`, {params: params});
  }
}
