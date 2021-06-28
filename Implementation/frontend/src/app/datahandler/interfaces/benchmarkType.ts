/**
 * Encapsulates information about a benchmark type, namely what its name is, and what is plottable.
 */
interface BenchmarkType {

  /**
   * The name of the benchmark type
   */
  get name(): string;

  /**
   * Permissible keys of the benchmark result that can be used for the x-axis
   */
  get validXKeys(): string[];

  /**
   * Permissible keys of the benchmark result that can be used for the y-axis
   */
  get validYKeys(): string[];
}
