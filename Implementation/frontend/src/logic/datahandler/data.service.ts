import {Injectable} from '@angular/core';
import {Commit} from "./interfaces/commit";
import {Benchmark} from "./interfaces/benchmark";
import {BenchmarkComparison} from "./interfaces/benchmark-comparison";
import {RestService} from "./rest.service";
import {BenchmarkComparisonPlotComponent} from "../../app/main-content/benchmark-comparison-plot/benchmark-comparison-plot.component";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private readonly URL: string = "backend.soos";

  getBranchNames(): string[] {
    return ["branch1", "branch2", "branch3"];
  }

  getCommitHistory(): Commit[] {
    return [];
  }

  getBenchmarks<T extends Benchmark>(commits: Commit[]): T[] {
    return [];
  }

/*  getBenchmarkComparison(b1: Benchmark, b2: Benchmark): BenchmarkComparison {
    return {};
  }*/

  constructor(private readonly restService: RestService) {
    this.restService.withURL = this.URL;
  }
}
