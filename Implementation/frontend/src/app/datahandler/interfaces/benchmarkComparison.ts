/**
 * Encapsulates information about the comparison between two Benchmark results
 */
interface BenchmarkComparison {

  /**
   * The benchmark results that are compared
   */
  get benchmarks(): Benchmark[];

  /**
   * Contains information like what the permissible keys to compare for are
   */
  get plotTypes(): PlotType[];
}
