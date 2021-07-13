import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScatterPlotComponent } from './plotviews/scatter-plot/scatter-plot.component';
import { BarPlotComponent } from './plotviews/bar-plot/bar-plot.component';
import { LinePlotComponent } from './plotviews/line-plot/line-plot.component';
import { SummaryChartComponent } from './plotviews/summary-chart/summary-chart.component';
import {NgxChartsModule} from "@swimlane/ngx-charts";



@NgModule({
  declarations: [
    ScatterPlotComponent,
    BarPlotComponent,
    LinePlotComponent,
    SummaryChartComponent
  ],
    exports: [
        LinePlotComponent,
        BarPlotComponent
    ],
  imports: [
    CommonModule,
    NgxChartsModule
  ]
})
export class LibModule { }
