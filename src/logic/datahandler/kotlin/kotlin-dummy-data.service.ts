import { Injectable } from "@angular/core";
import { DataHandler } from "../interfaces/data-handler";

// FIXME: find a way to get rid of that ts-ignore
// @ts-ignore
import * as parkview from "../../../../node_modules/parkview";
// @ts-ignore
import { kotlin } from "kotlin";
import { Observable, of } from "rxjs";
import { Device } from "../interfaces/device";
import { Commit } from "../interfaces/commit";
import { PlotTypeOption } from "../../plothandler/interfaces/available-plot-types";
import { PlotData } from "../../plothandler/interfaces/plot-data";
import { PlotConfiguration } from "../../plothandler/interfaces/plot-configuration";
import { Summary } from "../interfaces/summary";

function requestRawGithubContent(url: string): string {
  let xmlHttpReq = new XMLHttpRequest();
  xmlHttpReq.open("GET", url, false);
  xmlHttpReq.send(null);
  return xmlHttpReq.responseText;
}

class TsRepoHandler implements parkview.git.RepositoryHandler {
  numCommitsPerPage: number;

  constructor(numCommits: number) {
    this.numCommitsPerPage = numCommits;
  }

  fetchGitHistoryByBranch(branch: string, page: number, benchmarkType: any) {
    let content = requestRawGithubContent(
      `https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/${branch}`
    );
    let lines = content
      .split("\n")
      .map((x) => x.trim())
      .filter((x) => x.length > 0);
    let commits = lines.map((e) => {
      let elements = e.split(";;");
      return new parkview.git.Commit(
        elements[0],
        elements[3],
        Date.parse(elements[2]),
        elements[1]
      );
    });
    return commits.splice(
      (page - 1) * this.numCommitsPerPage,
      page * this.numCommitsPerPage
    );
  }

  getAvailableBranches(): string[] {
    let content = requestRawGithubContent(
      "https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/branches"
    );
    return content
      .split("\n")
      .map((x) => x.trim())
      .filter((x) => x.length > 0);
  }

  getNumberOfPages(branch: string) {
    let content = requestRawGithubContent(
      `https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/${branch}`
    );
    let lines = content.split("\n");
    return Math.ceil(lines.length / this.numCommitsPerPage);
  }
}

class TsDatabaseHandler implements parkview.database.DatabaseHandler {
  index: any;

  constructor() {
    let indexContent: string[] = requestRawGithubContent(
      "https://raw.githubusercontent.com/pse-parkview/parkview-data/main/benchmark_data/index"
    )
      .split("\n")
      .filter((x) => x.length > 0);
    indexContent.shift();
    this.index = indexContent.map((e: any) => {
      let benchmark, commit, device, file;
      [benchmark, commit, device, file] = e.split(",");
      return {
        benchmark: benchmark,
        commit: commit,
        device: device,
        file: file,
      };
    });
  }

  // TODO: can be cut
  insertBenchmarkResult(results: any) {
    throw new Error("should not be called");
  }

  fetchBenchmarkResult(commit: any, device: any, benchmark: any) {
    for (let point of this.index) {
      if (
        point.benchmark == benchmark &&
        point.commit == commit.sha &&
        point.device == device.name
      ) {
        let content = requestRawGithubContent(
          `https://raw.githubusercontent.com/pse-parkview/parkview-data/main/benchmark_data/${point.file}`
        );
        let spmvResult = parseBenchmarkResult(
          benchmark,
          commit,
          device,
          JSON.parse(content.replace(/\bNaN\b/g, "null"))
        );
        return spmvResult;
      }
    }
    throw new parkview.database.MissingBenchmarkResultException(
      commit,
      device,
      benchmark
    );
  }

  hasDataAvailable(commit: any, device: any, benchmark: any) {
    return (
      this.index.filter(
        (e: any) =>
          e.commit == commit.sha &&
          e.device == device.name &&
          e.benchmark == benchmark
      ).size > 0
    );
  }

  getAvailableDevicesForCommit(commit: any, benchmark: any) {
    return this.index
      .filter((e: any) => e.commit == commit.sha && e.benchmark == benchmark)
      .map((e: any) => new parkview.git.Device(e.device));
  }
}

function parseBenchmarkResult(
  benchmark: any,
  commit: any,
  device: any,
  content: object[]
) {
  switch (benchmark) {
    case parkview.git.BenchmarkType.Spmv:
      return parseSpmv(commit, device, content);
    case parkview.git.BenchmarkType.Conversion:
      return parseConversion(commit, device, content);
    case parkview.git.BenchmarkType.Preconditioner:
      return parsePreconditioner(commit, device, content);
    case parkview.git.BenchmarkType.Solver:
      return parseSolver(commit, device, content);
    case parkview.git.BenchmarkType.Blas:
      return parseBlas(commit, device, content);
    default:
      throw Error(`Invalid benchmark type ${benchmark}`);
  }
}

function parseSpmv(commit: any, device: any, content: object[]) {
  return new parkview.benchmark.SpmvBenchmarkResult(
    commit,
    device,
    new kotlin.collections.ArrayList(
      content.map((e: any) => {
        return new parkview.benchmark.SpmvDatapoint(
          e.problem.name,
          e.problem.rows,
          e.problem.cols,
          e.problem.nonzeros,
          new kotlin.collections.ArrayList(
            Object.entries(e.spmv).map((e: any) => {
              return new parkview.benchmark.Format(
                e[0],
                e[1].time,
                e[1].completed,
                e[1].storage,
                e[1].max_relative_norm2
              );
            })
          )
        );
      })
    )
  );
}

function parseConversion(commit: any, device: any, content: object[]) {
  return new parkview.benchmark.ConversionBenchmarkResult(
    commit,
    device,
    new kotlin.collections.ArrayList(
      content.map((e: any) => {
        return new parkview.benchmark.ConversionDatapoint(
          e.problem.name,
          e.problem.rows,
          e.problem.cols,
          e.problem.nonzeros,
          new kotlin.collections.ArrayList(
            Object.entries(e.spmv).map((e: any) => {
              return new parkview.benchmark.Conversion(
                e[0],
                e[1].time,
                e[1].completed
              );
            })
          )
        );
      })
    )
  );
}

function parseSolver(commit: any, device: any, content: object[]) {
  return new parkview.benchmark.SolverBenchmarkResult(
    commit,
    device,
    new kotlin.collections.ArrayList(
      content.map((e: any) => {
        return new parkview.benchmark.SolverDatapoint(
          e.problem.name,
          e.problem.rows,
          e.problem.cols,
          e.problem.nonzeros,
          e.optimal.spmv,
          new kotlin.collections.ArrayList(
            Object.entries(e.solver).map((e: any) => {
              return new parkview.benchmark.Solver(
                e[0],
                new kotlin.collections.ArrayList(e[1].recurrent_residuals),
                new kotlin.collections.ArrayList(e[1].true_residuals),
                new kotlin.collections.ArrayList(e[1].implicit_residuals),
                new kotlin.collections.ArrayList(e[1].iteration_timestamps),
                e[1].rhs_norm,
                e[1].residual_norm,
                e[1].completed,
                new kotlin.collections.ArrayList(
                  Object.entries(e[1].generate.components).map(
                    (c: any) => new parkview.benchmark.Component(c[0], c[1])
                  )
                ),
                e[1].generate.time,
                new kotlin.collections.ArrayList(
                  Object.entries(e[1].apply.components).map(
                    (c: any) => new parkview.benchmark.Component(c[0], c[1])
                  )
                ),
                e[1].apply.time,
                e[1].apply.iterations
              );
            })
          ),
          new kotlin.collections.ArrayList(
            Object.entries(e.spmv).map((e: any) => {
              return new parkview.benchmark.Format(
                e[0],
                e[1].time,
                e[1].completed,
                e[1].storage,
                e[1].max_relative_norm2
              );
            })
          )
        );
      })
    )
  );
}

// i dont believe in its existence, will implement it as soon as i've got testdata
function parsePreconditioner(commit: any, device: any, content: object[]) {}

function parseBlas(commit: any, device: any, content: object[]) {
  return new parkview.benchmark.BlasBenchmarkResult(
    commit,
    device,
    new kotlin.collections.ArrayList(
      content.map((e: any) => {
        return new parkview.benchmark.BlasDatapoint(
          e.n,
          e.r,
          e.m,
          e.k,
          new kotlin.collections.ArrayList(
            Object.entries(e.blas).map((e: any) => {
              return new parkview.benchmark.Operation(
                e[0],
                e[1].time,
                e[1].flops,
                e[1].bandwidth,
                e[1].completed,
                e[1].repetitions
              );
            })
          )
        );
      })
    )
  );
}

@Injectable({
  providedIn: "root",
})
export class KotlinDummyDataService implements DataHandler {
  private rest: parkview.rest.RestHandler;

  constructor() {
    // console.log(kotlin);
    let numberOfCommitsPerPage = 40;
    let dbHandler = new TsDatabaseHandler();
    let repHandler = new parkview.git.CachingRepositoryHandler(
      new parkview.database.AnnotatingRepositoryHandler(
        new TsRepoHandler(numberOfCommitsPerPage),
        dbHandler
      ),
      20,
      5,
      5,
      2
    );

    this.rest = new parkview.rest.ParkviewApiHandler(repHandler, dbHandler);
  }

  getAvailableDevicesForAveragePerformance(
    benchmarkType: string,
    branchName: string
  ): Observable<Device[]> {
    let benchmark;
    switch (benchmarkType) {
      case "Spmv":
        benchmark = parkview.git.BenchmarkType.Spmv;
        break;
      case "Blas":
        benchmark = parkview.git.BenchmarkType.Blas;
        break;
      case "Conversion":
        benchmark = parkview.git.BenchmarkType.Conversion;
        break;
      case "Preconditioner":
        benchmark = parkview.git.BenchmarkType.Preconditioner;
        break;
      case "Solver":
        benchmark = parkview.git.BenchmarkType.Solver;
        break;
      default:
        // unreachable if frontend UI is correct
        Error("Invalid Benchmark Type");
    }
    let devices: Device[] = this.rest.getAvailableDevices(
      branchName,
      benchmark
    );
    return of(devices);
  }

  getAvailablePlots(
    benchmarkType: string,
    commits: Commit[],
    devices: string[]
  ): Observable<PlotTypeOption[]> {
    // FIXME: weird $receiver.iterator Error when trying to get Data:
    let _commits = commits.map((element) => element.sha);
    let plots = this.rest
      .getAvailablePlots(benchmarkType, _commits, devices)
      .map((e: any) => {
        return {
          plotName: e.plotName,
          plottableAs: e.plottableAs.map((m: any) => m.name),
          options: e.options,
        };
      });
    return of(plots);
  }

  getAveragePerformance(
    benchmarkType: string,
    branchName: string,
    device: string
  ): Observable<PlotData> {
    let data: PlotData = this.rest.getAveragePerformance(
      branchName,
      benchmarkType,
      device
    );
    return of(data);
  }

  getBenchmarks(): Observable<string[]> {
    let benchmarks: string[] = this.rest.getAvailableBenchmarks();
    return of(benchmarks);
  }

  getBranchNames(): Observable<string[]> {
    let branches: string[] = this.rest.getAvailableBranches();
    return of(branches);
  }

  getCommitHistory(
    branchName: string,
    benchmarkType: string,
    page: number
  ): Observable<Commit[]> {
    let hist: Commit[] = this.rest.getHistory(branchName, page, benchmarkType);
    return of(hist);
  }

  getNumPages(branchName: string): Observable<number> {
    let num: number = this.rest.getNumberOfPages(branchName);
    return of(num);
  }

  getPlotData(config: PlotConfiguration): Observable<PlotData> {
    let benchmark: string = config.benchmark;
    let shas: string[] = config.commits;
    let devices: string[] = config.devices;
    let plotType: string = config.plotType;
    let plotParams = Object.entries(config.options).map((e) => new kotlin.Pair(e[0], e[1]));
    let plotParamsMap = new kotlin.collections.HashMap();
    // TODO: fix name wrangling somehow
    kotlin.collections.HashMap_init_q3lmfv$(plotParamsMap);
    plotParams.forEach((e: any) => {
      plotParamsMap.put_xwzc9p$(e.first, e.second);
    });

    let data: PlotData = this.rest.getPlot(
      benchmark,
      shas,
      devices,
      plotType,
      plotParamsMap
    );
    return of(data);
  }

  getSummary(
    benchmarkType: string,
    commitSha: string,
    device: string
  ): Observable<Summary> {
    // FIXME: returns some type of LinkedHashMap, turn that into a ES6 Map or something
    let summary: Summary = this.rest.getSummaryValue(
      benchmarkType,
      commitSha,
      device
    );
    return of(summary);
  }
}
