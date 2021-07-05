import {Point} from "./point"

/**
 * The data format for a Line Plot
 */
export interface Data {

  /**
   * The name that appears in the legend
   */
  "name": string,

  /**
   * An ordered list of x and y value pairs
   */
  "series": Point[]
}
