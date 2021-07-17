import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {GitHistoryComponent} from './main-content/dashboard/git-history/git-history.component';
import {SingleBenchmarkPlotComponent} from './main-content/single-benchmark-plot/single-benchmark-plot.component';
import {BenchmarkComparisonPlotComponent} from './main-content/benchmark-comparison-plot/benchmark-comparison-plot.component';
import {AveragePerformanceComponent} from './main-content/dashboard/average-performance/average-performance.component';
import {PlotConfigurationDialogComponent} from './dialogs/plot-configuration-dialog/plot-configuration-dialog.component';
import {BenchmarkSelectDialogComponent} from './dialogs/benchmark-select-dialog/benchmark-select-dialog.component';
import {LoadFromTemplateDialogComponent} from './dialogs/load-from-template-dialog/load-from-template-dialog.component';
import {ErrorDialogComponent} from './dialogs/error-dialog/error-dialog.component';
import {BenchmarkCompareDialogComponent} from './dialogs/benchmark-compare-dialog/benchmark-compare-dialog.component';
import {CookieConsentDialogComponent} from './dialogs/cookie-consent-dialog/cookie-consent-dialog.component';
import {SideCurrentChosenCommitComponent} from './sidebar/side-current-chosen-commit/side-current-chosen-commit.component';
import {SideBenchmarkCompareComponent} from './sidebar/side-benchmark-compare/side-benchmark-compare.component';
import {SideLoadFromTemplateComponent} from './sidebar/side-load-from-template/side-load-from-template.component';
import {SidePreviousPlotsComponent} from './sidebar/side-previous-plots/side-previous-plots.component';
import {PlotCardComponent} from './sidebar/side-previous-plots/plot-card/plot-card.component';
import {LibModule} from "../lib/lib.module";
import {LogicModule} from "../logic/logic.module";
import {DashboardComponent} from './main-content/dashboard/dashboard.component';
import {TestComponent} from './main-content/test/test.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatDialogModule} from "@angular/material/dialog";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatButtonModule} from "@angular/material/button";
import {CookieService} from "../logic/cookiehandler/cookie.service";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import {ChartsModule} from "ng2-charts";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { AbbreviatePipe } from './pipes/abbreviate.pipe';
import {FormsModule} from "@angular/forms";

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
    CookieConsentDialogComponent,
    SideCurrentChosenCommitComponent,
    SideBenchmarkCompareComponent,
    SideLoadFromTemplateComponent,
    SidePreviousPlotsComponent,
    PlotCardComponent,
    DashboardComponent,
    TestComponent,
    AbbreviatePipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LibModule,
    LogicModule,
    BrowserAnimationsModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatButtonModule,
    MatCardModule,
    MatMenuModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    ChartsModule,
    MatExpansionModule,
    MatCheckboxModule,
    FormsModule,
  ],
  providers: [
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
