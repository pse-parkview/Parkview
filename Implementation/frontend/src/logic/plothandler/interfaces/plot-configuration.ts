/**
 * Encapsulates information regarding a plot
 */
export interface PlotConfiguration {
  get benchmark(): string;
  get commits(): string[];
  get devices(): string[];
  get plotType(): string;
  get options(): Map<string, string>;
}
