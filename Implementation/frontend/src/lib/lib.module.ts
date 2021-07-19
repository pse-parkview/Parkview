import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgxChartsModule} from "@swimlane/ngx-charts";
import { DefaultvaluePipe } from './pipes/defaultvalue.pipe';



@NgModule({
  declarations: [
    DefaultvaluePipe
  ],
  exports: [
    DefaultvaluePipe
  ],
  imports: [
    CommonModule,
    NgxChartsModule
  ]
})
export class LibModule { }
