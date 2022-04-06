import { Injectable } from '@angular/core';
import {DataHandler} from "../interfaces/data-handler";

// FIXME: find a way to get rid of that ts-ignore
// @ts-ignore
import * as parkview from "../../../../node_modules/parkview"
import {Observable, of} from "rxjs";
import {Device} from "../interfaces/device";
import {Commit} from "../interfaces/commit";
import {PlotTypeOption} from "../../plothandler/interfaces/available-plot-types";
import {PlotData} from "../../plothandler/interfaces/plot-data";
import {PlotConfiguration} from "../../plothandler/interfaces/plot-configuration";
import {Summary} from "../interfaces/summary";

function requestRawGithubContent(url: string): string {
  let xmlHttpReq = new XMLHttpRequest();
  xmlHttpReq.open("GET", url, false);
  xmlHttpReq.send(null);
  return xmlHttpReq.responseText;
}

class TestRepoHandler implements parkview.com.parkview.parkview.git.RepositoryHandler {
  repHandler;
  numCommitsPerPage = 40;

  constructor() {
    console.log(parkview.com.parkview.parkview.git.RepositoryHandler);
    this.repHandler = new parkview.com.parkview.parkview.rest.DummyRepositoryHandler();
  }

  fetchGitHistoryByBranch(branch: string, page: number, benchmarkType: any) {
    let content = requestRawGithubContent(`https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/${branch}`);
    let lines = content.split("\n").map(x => x.trim()).filter(x => x.length > 0);
    console.log(this.repHandler.fetchGitHistoryByBranch("test", page, benchmarkType));
    let commits = lines.map(e => {
      let elements = e.split(";;");
      return new parkview.com.parkview.parkview.git.Commit(elements[0], elements[3], Date.parse(elements[2]), elements[1]);
    });
    return commits.splice((page - 1) * this.numCommitsPerPage, page * this.numCommitsPerPage);
  }

  getAvailableBranches(): string[] {
    let content = requestRawGithubContent("https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/branches");
    return content.split("\n").map(x => x.trim()).filter(x => x.length > 0);
  }

  getNumberOfPages(branch: string) {
    let content = requestRawGithubContent(`https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/${branch}`);
    let lines = content.split("\n");
    return Math.ceil(lines.length / this.numCommitsPerPage);
  }

}

@Injectable({
  providedIn: 'root'
})
export class KotlinDummyDataService implements DataHandler {
  private rest: parkview.com.parkview.parkview.rest.RestHandler;

  constructor() {
    let repHandler = new TestRepoHandler();
    let dbHandler = new parkview.com.parkview.parkview.rest.DummyDatabaseHandler();

    this.rest = new parkview.com.parkview.parkview.rest.ParkviewApiHandler(repHandler, dbHandler);
  }

  getAvailableDevicesForAveragePerformance(benchmarkType: string, branchName: string): Observable<Device[]> {
    let benchmark;
    switch (benchmarkType) {
      case "Spmv":
        benchmark = parkview.com.parkview.parkview.git.BenchmarkType.Spmv;
        break;
      case "Blas":
        benchmark = parkview.com.parkview.parkview.git.BenchmarkType.Blas;
        break;
      case "Conversion":
        benchmark = parkview.com.parkview.parkview.git.BenchmarkType.Conversion;
        break;
      case "Preconditioner":
        benchmark = parkview.com.parkview.parkview.git.BenchmarkType.Preconditioner;
        break;
      case "Solver":
        benchmark = parkview.com.parkview.parkview.git.BenchmarkType.Solver;
        break;
      default:
        // unreachable if frontend UI is correct
        Error("Invalid Benchmark Type");
    }
    let devices: Device[] = this.rest.getAvailableDevices(branchName, benchmark);
    return of(devices);
  }

  getAvailablePlots(benchmarkType: string, commits: Commit[], devices: string[]): Observable<PlotTypeOption[]> {
    // FIXME: weird $receiver.iterator Error when trying to get Data:
    let _commits = commits.map(element => element.sha);
    let plots = this.rest.getAvailablePlots("Spmv", _commits, devices);
    console.log(plots);
    let _plots = [];
    for (let e of plots) {
      _plots.push({
        plotName: e.plotName,
        plottableAs: e.plottableAs.map((e: any) => e.name),
        options: e.options
      });
    }
    // console.log(_plots)
    return of(_plots);

  }

  getAveragePerformance(benchmarkType: string, branchName: string, device: string): Observable<PlotData> {
    let data: PlotData = this.rest.getAveragePerformance(branchName, benchmarkType, device);
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

  getCommitHistory(branchName: string, benchmarkType: string, page: number): Observable<Commit[]> {
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
    let plotParams: Map<string, string> = new Map(Object.entries(config.options)); // pls
    let data: PlotData = this.rest.getPlot(benchmark, shas, devices, plotType, plotParams);
    return of(data);
  }

  getSummary(benchmarkType: string, commitSha: string, device: string): Observable<Summary> {
    // FIXME: returns some type of LinkedHashMap, turn that into a ES6 Map or something
    let summary: Summary = this.rest.getSummaryValue(benchmarkType, commitSha, device);
    return of(summary);
  }
}
