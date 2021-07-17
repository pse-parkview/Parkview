import {Commit} from "../../datahandler/interfaces/commit";

/**
 * Encapsulates information regarding a plot
 */
export interface PlotConfiguration {
  get benchmark(): string;
  get commits(): Commit[];
  get devices(): string[];
  get plotType(): string;
  get xAxis(): string;
}
