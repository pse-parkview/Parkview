export interface AvailablePlotTypes {
  get line(): PlotTypeOption[];
  get scatter(): PlotTypeOption[];
  get bar(): PlotTypeOption[];
  get stackedBar(): PlotTypeOption[];
}

export interface PlotTypeOption {
  get plotName(): string;
  get options(): PlotOption[];
}

export interface PlotOption {
  get name(): string;
  get options(): string[];
  get number(): boolean;
}

export const X_AXIS_PLOT_OPTION_NAME = 'xAxis';
export const Y_AXIS_PLOT_OPTION_NAME = 'yAxis';
