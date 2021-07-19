import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ChartDataSets} from "chart.js";
import {AvailablePlotTypes} from "../plothandler/interfaces/available-plot-types";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private readonly url: string = "/backend";

  constructor(private readonly http: HttpClient) {
  }

  getBranchNames(): Observable<string[]> {
    return this.http.get<Array<string>>(this.url + '/branches');
    // uncomment if you dont have a backend to test with // return of(["branch1", "branch2", "branch3"]);
  }

  getBenchmarks() {
    return this.http.get<Array<string>>(`${this.url}/benchmarks`);
  }

  getCommitHistory(branchName: string, benchmarkType: string, page: number = 1): Observable<Commit[]> {
    const params: HttpParams = new HttpParams()
      .set('branch', branchName)
      .set('page', page.toString(10))
      .set('benchmark', benchmarkType);
    return this.http.get<Array<Commit>>(`${this.url}/history`, {params: params})

    /* uncomment and maybe extend if you dont have a backend to test with

    return of([
      {
        date: new Date(),
        commitMessage: `mockcommit 1 from ${branchName}`,
        author: 'max',
        hasBenchmark: false,
        sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
        branch: branchName
      },
      {
        date: new Date(),
        commitMessage: `mockcommit 2 from ${branchName}`,
        author: 'ted',
        hasBenchmark: true,
        sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
        branch: branchName
      },
      {
        date: new Date(),
        commitMessage: `mockcommit 3 from ${branchName}`,
        author: 'rstallman',
        hasBenchmark: true,
        sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
        branch: branchName
      },
    ]);
    */
  }

  getAvailablePlots(benchmarkType: string, commits: Commit[], devices: string[]): Observable<AvailablePlotTypes> {
    let params: HttpParams = new HttpParams()
      .set('benchmark', benchmarkType);
    commits.forEach(c => params = params.append('shas', c.sha));
    devices.forEach(d => params = params.append('devices', d));

    return this.http.get<AvailablePlotTypes>(`${this.url}/availablePlots`, {params: params});
  }

  getPlotData(config: PlotConfiguration): Observable<ChartDataSets[]> {
    let params: HttpParams = new HttpParams()
      .set('benchmark', config.benchmark)
      .set('plotType', config.plotType)
    config.commits.forEach(sha => params = params.append('shas', sha));
    config.devices.forEach(d => params = params.append('devices', d));
    config.options.forEach(((value, key) => params = params.append(key, value)));
    return this.http.get<Array<ChartDataSets>>(`${this.url}/plot`, {params: params});
  }
}
