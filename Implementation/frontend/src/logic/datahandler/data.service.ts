import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {Benchmark} from "./interfaces/benchmark";
import {BenchmarkComparison} from "./interfaces/benchmark-comparison";
import {RestService} from "./rest.service";
import {BenchmarkComparisonPlotComponent} from "../../app/main-content/benchmark-comparison-plot/benchmark-comparison-plot.component";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable, of} from "rxjs";
import {Data} from "../plothandler/interfaces/data";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private readonly URL: string = "backend.soos";

  constructor(private readonly restService: RestService) {
  }

  getBranchNames(): Observable<string[]> {
    return of(["branch1", "branch2", "branch3"]);
  }

  getCommitHistory(branchName: string): Observable<Commit[]> {
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
  }

  getBenchmarks<T extends Benchmark>(commits: Commit[]): Observable<T[]> {
    return of([]);
  }

  getData(config: PlotConfiguration): Observable<Data[]> {
     return this.restService.makeRequest(config, this.URL);
  }

}
