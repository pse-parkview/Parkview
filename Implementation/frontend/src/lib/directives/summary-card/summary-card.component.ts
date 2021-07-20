import {Component, Input, OnInit} from '@angular/core';
import {Pair} from "../../../logic/commit-selection-handler/interfaces/pair";
import {Commit} from "../../../logic/datahandler/interfaces/commit";
import {DataService} from "../../../logic/datahandler/data.service";
import {Summary} from "../../../logic/datahandler/interfaces/summary";

@Component({
  selector: 'app-summary-card',
  templateUrl: './summary-card.component.html',
  styleUrls: ['./summary-card.component.scss']
})
export class SummaryCardComponent implements OnInit {

  @Input()
  commitSha: string = '';

  @Input()
  device: string = '';

  @Input()
  benchmarkType: string = '';

  summary: Summary = {};

  constructor(private readonly dataService: DataService) {
  }

  ngOnInit(): void {
    this.dataService.getSummary(this.commitSha, this.device, this.benchmarkType).subscribe((s: Summary) => this.summary = s);
  }
}
