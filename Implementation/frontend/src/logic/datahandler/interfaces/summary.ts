/**
 *  Encapsulates information regarding the summary of a benchmark
 */
export interface Summary {

  /**
   * The different values associated to theit name or key
   */
  get summary(): Map<any, any>;

  /**
   * The amount tested
   */
  get amountTested(): number;
}
