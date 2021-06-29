import {PlotType} from "./plot-type";
import {BenchmarkComparison} from "../../datahandler/interfaces/benchmark-comparison"
import {Benchmark} from "../../datahandler/interfaces/benchmark";

/**
 * Encapsulates information regarding a plot
 */
export interface PlotConfiguration {
  get plotType(): PlotType;
  get benchmark(): Benchmark;
  get isComparison(): boolean;
  get benchmarkComparison(): BenchmarkComparison;
}
