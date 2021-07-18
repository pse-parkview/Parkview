export interface AvailablePlotTypes {
  get line(): NameWithAvailableXAxis[];
  get scatter(): NameWithAvailableXAxis[];
  get bar(): NameWithAvailableXAxis[];
  get stackedBar(): NameWithAvailableXAxis[];
}

export interface NameWithAvailableXAxis {
  get name(): string;
  get xAxis(): string[];
}
