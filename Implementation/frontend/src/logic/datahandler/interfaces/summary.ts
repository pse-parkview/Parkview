/**
 *  Encapsulates information regarding the summary of a benchmark
 */
export interface Summary {

  /**
   * The different values associated to their name or key
   */
  get summary(): Map<string, number>;

  /**
   * The amount tested
   */
  get amountTested(): number;
}
