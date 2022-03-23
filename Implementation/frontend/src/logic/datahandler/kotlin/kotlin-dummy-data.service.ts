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

@Injectable({
  providedIn: 'root'
})
export class KotlinDummyDataService implements DataHandler {
  private rest: parkview.com.parkview.parkview.rest.RestHandler;

  constructor() {
    let repHandler = new parkview.com.parkview.parkview.rest.DummyRepositoryHandler();
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
    // let commits : string[] = [new parkview.com.parkview.parkview.git.Commit("peter").sha];
    // let devices : string[] = [new parkview.com.parkview.parkview.git.Device("meter").name];
    // let plots = rest.getAvailablePlots("Spmv", commits, devices) // ERROR
    let plots: PlotTypeOption[] = Array();
    return of(plots);

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
