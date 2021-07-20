import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import { DefaultvaluePipe } from './pipes/defaultvalue.pipe';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {AbbreviatePipe} from "./pipes/abbreviate.pipe";



@NgModule({
  declarations: [
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
  ]
})
export class LibModule { }
