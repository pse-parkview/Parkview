import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import { DefaultvaluePipe } from './pipes/defaultvalue.pipe';
import {MatSnackBarModule} from "@angular/material/snack-bar";



@NgModule({
  declarations: [
    DefaultvaluePipe
  ],
  exports: [
    DefaultvaluePipe
  ],
  imports: [
    CommonModule,
    NgxChartsModule,
    MatSnackBarModule,
  ]
})
export class LibModule { }
