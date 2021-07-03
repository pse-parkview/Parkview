import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./main-content/dashboard/dashboard.component";
import {SingleBenchmarkPlotComponent} from "./main-content/single-benchmark-plot/single-benchmark-plot.component";
import {BenchmarkComparisonPlotComponent} from "./main-content/benchmark-comparison-plot/benchmark-comparison-plot.component";
import {TestComponent} from "./main-content/test/test.component";

const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'test', component: TestComponent },
  { path: 'benchmark-single', component: SingleBenchmarkPlotComponent },
  { path: 'benchmark-comparison', component: BenchmarkComparisonPlotComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
