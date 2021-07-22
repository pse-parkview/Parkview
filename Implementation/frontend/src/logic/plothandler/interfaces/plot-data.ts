import {ChartDataSets} from "chart.js";
import {Label} from "ng2-charts";

export interface PlotData {
  get datasets(): ChartDataSets[];
  get labels(): Label[];
}
