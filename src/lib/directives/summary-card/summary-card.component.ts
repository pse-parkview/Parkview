import {Component, Input, OnInit} from '@angular/core';
import {Summary} from "../../../logic/datahandler/interfaces/summary";
import {ParkviewLibDataService} from "../../../logic/datahandler/kotlin/parkview-lib-data.service";

export interface SummaryConfig {
  get benchmarkName(): string;

  get commitSha(): string;

  get device(): string;
}

@Component({
  selector: 'app-summary-card',
  templateUrl: './summary-card.component.html',
  styleUrls: ['./summary-card.component.scss']
})
export class SummaryCardComponent implements OnInit {

  @Input()
  config: SummaryConfig = {
    benchmarkName: '',
    commitSha: '',
    device: '',
  };

  summary: Summary = {};
  summaryData: { key: string, value: string }[] = [];

  constructor(private readonly dataService: ParkviewLibDataService) {
  }

  ngOnInit(): void {
    this.dataService.getSummary(this.config.benchmarkName, this.config.commitSha, this.config.device).subscribe((s: Summary) => {
      this.summary = s;
      console.log(this.summary);
      this.summaryData = SummaryCardComponent.compileSummaryData(s);
    });
  }

  private static compileSummaryData(summary: Summary): { key: string, value: string }[] {
    const output: { key: string, value: string }[] = [];
    Object.keys(summary).forEach(k => {
      output.push({key: k, value: summary[k].toLocaleString()});
    });
    return output;
  }
}
