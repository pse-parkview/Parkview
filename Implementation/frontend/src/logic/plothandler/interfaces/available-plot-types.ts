export interface AvailablePlotTypes {
  get line(): string[];
  get scatter(): string[];
  get bar(): string[];
  get stackedBar(): string[];
}
