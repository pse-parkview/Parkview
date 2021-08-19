import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {GitHistoryComponent} from './main-content/dashboard/git-history/git-history.component';
import {AveragePerformanceComponent} from './main-content/dashboard/average-performance/average-performance.component';
import {PlotConfigurationDialogComponent} from './dialogs/plot-configuration-dialog/plot-configuration-dialog.component';
import {CookieConsentDialogComponent} from './dialogs/cookie-consent-dialog/cookie-consent-dialog.component';
import {SideCurrentChosenCommitComponent} from './sidebar/side-current-chosen-commit/side-current-chosen-commit.component';
import {SideLoadFromTemplateComponent} from './sidebar/side-load-from-template/side-load-from-template.component';
import {SidePreviousPlotsComponent} from './sidebar/side-previous-plots/side-previous-plots.component';
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
import {FormsModule} from "@angular/forms";
import {MatTableModule} from "@angular/material/table";
import {MatInputModule} from "@angular/material/input";
import {LinePlotComponent} from './main-content/line-plot/line-plot.component';
import {ScatterPlotComponent} from './main-content/scatter-plot/scatter-plot.component';
import {BarPlotComponent} from './main-content/bar-plot/bar-plot.component';
import {MatRadioModule} from "@angular/material/radio";
import {MatSliderModule} from "@angular/material/slider";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {ClipboardModule} from "@angular/cdk/clipboard";
import {MatTooltipModule} from "@angular/material/tooltip";
import { ConfigButtonsComponent } from './main-content/config-buttons/config-buttons.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidebarComponent,
    GitHistoryComponent,
    AveragePerformanceComponent,
    PlotConfigurationDialogComponent,
    CookieConsentDialogComponent,
    SideCurrentChosenCommitComponent,
    SideLoadFromTemplateComponent,
    SidePreviousPlotsComponent,
    DashboardComponent,
    TestComponent,
    LinePlotComponent,
    ScatterPlotComponent,
    BarPlotComponent,
    ConfigButtonsComponent
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
        MatTableModule,
        MatInputModule,
        MatRadioModule,
        MatSliderModule,
        MatSidenavModule,
        MatToolbarModule,
        MatIconModule,
        ClipboardModule,
        MatTooltipModule,
    ],
  providers: [
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
