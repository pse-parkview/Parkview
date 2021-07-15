import {PlotType} from "./plot-type";
import {Benchmark} from "../../datahandler/interfaces/benchmark";
import {Commit} from "../../datahandler/interfaces/commit";

/**
 * Encapsulates information regarding a plot
 */
export interface PlotConfiguration {
  get plotType(): PlotType;
  get benchmark(): Benchmark;
  get commits(): Commit[];
  get devices(): string[];
}
