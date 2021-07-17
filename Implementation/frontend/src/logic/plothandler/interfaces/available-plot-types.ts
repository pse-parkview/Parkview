export interface AvailablePlotTypes {
  get line(): AvailableXAxis[];
  get scatter(): AvailableXAxis[];
  get bar(): AvailableXAxis[];
  get stackedBar(): AvailableXAxis[];
}

export interface AvailableXAxis {
  get name(): string;
  get xAxis(): string[];
}
