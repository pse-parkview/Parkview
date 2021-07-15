import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable, of} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ChartDataSets} from "chart.js";

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

  getBenchmarks() {
    return this.http.get<Array<string>>(`${this.url}/benchmarks`);
  }


  getPlotData(config: PlotConfiguration): Observable<ChartDataSets[]> {
    const params: HttpParams = new HttpParams()
      .set('benchmark', config.benchmark)
      .set('plotType', config.plotType);
    config.commits.forEach(c => params.append('shas', c.sha));
    config.devices.forEach(d => params.append('devices', d));

    return this.http.get<Array<ChartDataSets>>(`${this.url}/plot`, {params: params});
  }
}
