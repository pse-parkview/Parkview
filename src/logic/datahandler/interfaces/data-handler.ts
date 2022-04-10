/**
 *  Rest Handler
 */
import {Observable} from "rxjs";
import {Commit} from "./commit";
import {PlotTypeOption} from "../../plothandler/interfaces/available-plot-types";
import {PlotData} from "../../plothandler/interfaces/plot-data";
import {Device} from "./device";
import {Summary} from "./summary";
import {PlotConfiguration} from "../../plothandler/interfaces/plot-configuration";

export interface DataHandler {

  getBranchNames(): Observable<string[]>;

  getBenchmarks(): Observable<string[]>;

  getCommitHistory(branchName: string, benchmarkType: string): Iterator<Commit>;

  getAvailablePlots(benchmarkType: string, commits: Commit[], devices: string[]): Observable<PlotTypeOption[]>;

  getPlotData(config: PlotConfiguration): Observable<PlotData>;

  getSummary(benchmarkType: string, commitSha: string, device: string): Observable<Summary>;

  getAvailableDevicesForAveragePerformance(benchmarkType: string, branchName: string): Observable<Device[]>;

  getAveragePerformance(benchmarkType: string, branchName: string, device: string): Observable<PlotData>;

}
