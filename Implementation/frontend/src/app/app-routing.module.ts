import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from "./main-content/dashboard/dashboard.component";
import {TestComponent} from "./main-content/test/test.component";
import {BarPlotComponent} from "./main-content/bar-plot/bar-plot.component";
import {LinePlotComponent} from "./main-content/line-plot/line-plot.component";
import {ScatterPlotComponent} from "./main-content/scatter-plot/scatter-plot.component";

const routes: Routes = [
  {path: '', component: DashboardComponent},
  {path: 'test', component: TestComponent},
  {path: 'line', component: LinePlotComponent},
  {path: 'bar', component: BarPlotComponent},
  {path: 'scatter', component: ScatterPlotComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
