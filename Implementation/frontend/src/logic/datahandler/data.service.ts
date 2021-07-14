import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {Benchmark} from "./interfaces/benchmark";
import {BenchmarkComparison} from "./interfaces/benchmark-comparison";
import {RestService} from "./rest.service";
import {BenchmarkComparisonPlotComponent} from "../../app/main-content/benchmark-comparison-plot/benchmark-comparison-plot.component";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable, of} from "rxjs";
import {Data} from "../plothandler/interfaces/data";
import {HttpClient, HttpParams} from "@angular/common/http";

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

  getCommitHistory(branchName: string, benchmark: string|undefined = undefined, page: number = 1): Observable<Commit[]> {
    const params: HttpParams = new HttpParams()
      .set('branch', branchName)
      .set('page', page.toString(10))
      .set('benchmark', 'AMD');
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
    return this.http.get<Array<{
      name: string,
      type: string,
    }>>(`${this.url}/benchmarks`);
  }

  getData(config: PlotConfiguration): Observable<Data[]> {
    return this.http.get<Data[]>(this.url);
  }
}
