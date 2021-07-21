import {ChartDataSets} from "chart.js";

export interface PlotData {
  get datasets(): ChartDataSets[];
  get labels(): string[];
}
