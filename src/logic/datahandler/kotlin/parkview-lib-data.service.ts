import { Injectable } from "@angular/core";
import { DataHandler } from "../interfaces/data-handler";

// FIXME: find a way to get rid of that ts-ignore
// @ts-ignore
import * as parkview from "../../../../node_modules/parkview-lib/dist";
// @ts-ignore
import { kotlin } from "kotlin";
import { Observable, of } from "rxjs";
import { Device } from "../interfaces/device";
import { Commit } from "../interfaces/commit";
import { PlotTypeOption } from "../../plothandler/interfaces/available-plot-types";
import { PlotData } from "../../plothandler/interfaces/plot-data";
import { PlotConfiguration } from "../../plothandler/interfaces/plot-configuration";
import { Summary } from "../interfaces/summary";
import {BenchmarkParser} from "../BenchmarkParser";

function requestRawGithubContent(url: string): string {
  let xmlHttpReq = new XMLHttpRequest();
  xmlHttpReq.open("GET", url, false);
  xmlHttpReq.send(null);
  return xmlHttpReq.responseText;
}

class TsRepoHandler implements parkview.git.RepositoryHandler {
  base_url: string = "https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data";

  numCommitsPerPage: number;

  constructor(numCommits: number) {
    this.numCommitsPerPage = numCommits;
  }

  fetchGitHistoryByBranch(branch: string, page: number, benchmarkType: any) {
    let received = JSON.parse(
        requestRawGithubContent(`${this.base_url}/${branch}.json`)
    );
    let commits: parkview.git.Commit[] = [];

    for (let element of received) {
      commits.push(new parkview.git.Commit(
          element.sha,
          element.message,
          Date.parse(element.date),
          element.author
      ));
    }
    return commits;
  }

  getAvailableBranches(): string[] {
    return JSON.parse(
        requestRawGithubContent(`${this.base_url}/branches.json`)
    );
  }

  getNumberOfPages(branch: string) {
    let received = JSON.parse(
        requestRawGithubContent(`${this.base_url}/${branch}.json`)
    );
    return Math.ceil(received.length / this.numCommitsPerPage);
  }
}

class TsDatabaseHandler implements parkview.database.DatabaseHandler {
  base_url: string = "https://raw.githubusercontent.com/pse-parkview/parkview-data/main/benchmark_data";
  index: any = [];
  private benchmarkParser: BenchmarkParser;

  constructor() {
    this.benchmarkParser = new BenchmarkParser();
    let content = JSON.parse(requestRawGithubContent(`${this.base_url}/index.json`));

    for (let commit in content) {
      for (let benchmark in content[commit]) {
        for (let device of content[commit][benchmark]) {
          this.index.push({
            benchmark: benchmark,
            commit: commit,
            device: device,
          });
        }
      }
    }
  }

  // TODO: can be cut
  insertBenchmarkResult(results: any) {
    throw new Error("should not be called");
  }

  fetchBenchmarkResult(commit: any, device: any, benchmark: any) {
    for (let point of this.index) {
      if (
        point.benchmark == benchmark.name.toLowerCase() &&
        point.commit == commit.sha &&
        point.device == device.name
      ) {
        let file_path = `${point.commit}/${point.benchmark}/${point.device}.json`;
        let content = requestRawGithubContent(`${this.base_url}/${file_path}`);
        let spmvResult = this.benchmarkParser.parseBenchmarkResult(
          benchmark,
          commit,
          device,
          // TODO: how to deal with NaNs?
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
          e.commit === commit.sha &&
          e.device === device.name &&
          e.benchmark === benchmark.name.toLowerCase()
      ).size > 0
    );
  }

  getAvailableDevicesForCommit(commit: any, benchmark: any) {
    return this.index
      .filter((e: any) => e.commit === commit.sha && e.benchmark === benchmark.name.toLowerCase())
      .map((e: any) => new parkview.git.Device(e.device));
  }
}

@Injectable({
  providedIn: "root",
})
export class ParkviewLibDataService implements DataHandler {
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

  *getCommitHistory(
    branchName: string,
    benchmarkType: string,
  ): Iterator<Commit> {
    let hist: Commit[] = this.rest.getHistory(branchName, 0, benchmarkType);
    for (let commit of hist) {
      yield commit;
    }
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
    let summary: Summary = this.rest.getSummaryValue(
      benchmarkType,
      commitSha,
      device
    );
    return of(summary);
  }
}
