/**
 * Encapsulates information about the comparison between two Benchmark results
 */
import {Benchmark} from "./benchmark";
import {BenchmarkType} from "./benchmark-type";

export interface BenchmarkComparison {

  /**
   * The benchmark results that are compared
   */
  get benchmarks(): Benchmark[];

  /**
   * Contains information like what the permissible keys to compare for are
   */
  get plotTypes(): BenchmarkType[];
}
