import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {Benchmark} from "./interfaces/benchmark";
import {BenchmarkComparison} from "./interfaces/benchmark-comparison";
import {RestService} from "./rest.service";
import {BenchmarkComparisonPlotComponent} from "../../app/main-content/benchmark-comparison-plot/benchmark-comparison-plot.component";
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {Observable} from "rxjs";
import {Data} from "../plothandler/interfaces/data";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private readonly URL: string = "backend.soos";

  constructor(private readonly restService: RestService) {
  }

  getBranchNames(): string[] {
    return ["branch1", "branch2", "branch3"];
  }

  getCommitHistory(branchName: string): Commit[] {
    return [];
  }

  getBenchmarks<T extends Benchmark>(commits: Commit[]): T[] {
    return [];
  }

  getData(config: PlotConfiguration): Observable<Data[]> {
     return this.restService.makeRequest(config, this.URL);
  }

}
