/**
 * Represents a data point
 */
export interface DataPoint {

  /**
   * The name of the data point
   */
  get name(): string;

  /**
   * The x-axis label
   */
  get x(): string;

  /**
   * The y-axis label
   */
  get y(): number;
}
