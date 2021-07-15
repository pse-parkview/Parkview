import {PlotType} from "./plot-type";
import {Commit} from "../../datahandler/interfaces/commit";

/**
 * Encapsulates information regarding a plot
 */
export interface PlotConfiguration {
  get plotType(): PlotType;
  get benchmark(): string;
  get commits(): Commit[];
  get devices(): string[];
}
