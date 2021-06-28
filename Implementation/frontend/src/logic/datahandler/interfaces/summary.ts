/**
 *  Encapsulates information regarding the summary of a benchmark
 */
interface Summary {

  /**
   * The different values associated to theit name or key
   */
  get summary(): Map<K, V>;

  /**
   * The amount tested
   */
  get amountTested(): number;
}
