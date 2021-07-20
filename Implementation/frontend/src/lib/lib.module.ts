import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import { DefaultvaluePipe } from './pipes/defaultvalue.pipe';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { SummaryCardComponent } from './directives/summary-card/summary-card.component';
import {MatCardModule} from "@angular/material/card";
import {AppModule} from "../app/app.module";
import {MatTableModule} from "@angular/material/table";
import {AbbreviatePipe} from "./pipes/abbreviate.pipe";



@NgModule({
  declarations: [
    DefaultvaluePipe,
    SummaryCardComponent,
    AbbreviatePipe,
    DefaultvaluePipe,
  ],
  exports: [
    DefaultvaluePipe,
    AbbreviatePipe,
  ],
  imports: [
    CommonModule,
    NgxChartsModule,
    MatSnackBarModule,
    MatCardModule,
    MatTableModule,
  ]
})
export class LibModule { }
