/**
 * Encapsulates information regarding a plot
 */

export interface PlotConfiguration {
  get benchmark(): string;

  get commits(): string[];

  get devices(): string[];

  get plotType(): string;

  get labelForTitle(): string;

  get labelForXAxis(): string;

  get labelForYAxis(): string;

  get options(): { [key: string]: string };

  get chartType(): SupportedChartType;
}

export type SupportedChartType = 'line' | 'bar' | 'scatter';
