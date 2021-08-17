import {SupportedChartType} from "./plot-configuration";

export interface PlotTypeOption {
  get plotName(): string;
  get plottableAs(): SupportedChartType[];
  get options(): PlotOption[];
}

export interface PlotOption {
  get name(): string;
  get options(): string[];
  get number(): boolean;
  get default(): string;
  get description(): string;
}

export const X_AXIS_PLOT_OPTION_NAME = 'xAxis';
export const Y_AXIS_PLOT_OPTION_NAME = 'yAxis';
