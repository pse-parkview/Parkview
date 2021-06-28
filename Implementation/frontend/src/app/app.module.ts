import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { GitHistoryComponent } from './main-content/git-history/git-history.component';
import { SingleBenchmarkPlotComponent } from './main-content/single-benchmark-plot/single-benchmark-plot.component';
import { BenchmarkComparisonPlotComponent } from './main-content/benchmark-comparison-plot/benchmark-comparison-plot.component';
import { AveragePerformanceComponent } from './main-content/average-performance/average-performance.component';
import { PlotConfigurationDialogComponent } from './dialogs/plot-configuration-dialog/plot-configuration-dialog.component';
import { BenchmarkSelectDialogComponent } from './dialogs/benchmark-select-dialog/benchmark-select-dialog.component';
import { LoadFromTemplateDialogComponent } from './dialogs/load-from-template-dialog/load-from-template-dialog.component';
import { ErrorDialogComponent } from './dialogs/error-dialog/error-dialog.component';
import { BenchmarkCompareDialogComponent } from './dialogs/benchmark-compare-dialog/benchmark-compare-dialog.component';
import { CookieConsentDialogComponent } from './dialogs/cookie-consent-dialog/cookie-consent-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidebarComponent,
    GitHistoryComponent,
    SingleBenchmarkPlotComponent,
    BenchmarkComparisonPlotComponent,
    AveragePerformanceComponent,
    PlotConfigurationDialogComponent,
    BenchmarkSelectDialogComponent,
    LoadFromTemplateDialogComponent,
    ErrorDialogComponent,
    BenchmarkCompareDialogComponent,
    CookieConsentDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
