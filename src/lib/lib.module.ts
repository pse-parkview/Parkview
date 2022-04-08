import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import {DefaultvaluePipe} from './pipes/defaultvalue.pipe';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {SummaryCardComponent} from './directives/summary-card/summary-card.component';
import {MatCardModule} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {AbbreviatePipe} from "./pipes/abbreviate.pipe";
import {DateFormatterPipe} from './pipes/date-formatter.pipe';
import {AbbreviateShaPipe} from './pipes/abbreviate-sha.pipe';


@NgModule({
  declarations: [
    DefaultvaluePipe,
    SummaryCardComponent,
    AbbreviatePipe,
    DefaultvaluePipe,
    DateFormatterPipe,
    AbbreviateShaPipe,
  ],
  exports: [
    DefaultvaluePipe,
    AbbreviatePipe,
    SummaryCardComponent,
    DateFormatterPipe,
    AbbreviateShaPipe,
  ],
  imports: [
    CommonModule,
    NgxChartsModule,
    MatSnackBarModule,
    MatCardModule,
    MatTableModule,
  ]
})
export class LibModule {
}
