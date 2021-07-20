/**
 * Encapsulates information regarding a plot
 */
import {ChartType} from "chart.js";

export interface PlotConfiguration {
  get benchmark(): string;
  get commits(): string[];
  get devices(): string[];
  get plotType(): string;
  get options(): { [key: string]: string} ;
  get chartType(): SupportedChartType;
}
export type SupportedChartType= 'line' | 'bar' | 'scatter';
