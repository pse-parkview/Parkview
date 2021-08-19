import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PlotConfigurationDialogComponent} from './plot-configuration-dialog.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {CookieModule} from "ngx-cookie";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatInputModule} from "@angular/material/input";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('PlotConfigurationDialogComponent', () => {
  let component: PlotConfigurationDialogComponent;
  let fixture: ComponentFixture<PlotConfigurationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        PlotConfigurationDialogComponent
      ],
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        MatDialogModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatCheckboxModule,
        MatSnackBarModule,
        CookieModule.forRoot(),
      ],
      providers: [
        CookieService
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlotConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should recognize valid selections', () => {
    component.benchmarkName = 'testName';
    component.commitsAndDevices = [];
    component.currentPlotTypeOption = {
      options: [],
      plottableAs: [],
      plotName: 'testPlotType'
    };
    component.plotlabelTitle = 'mockButValidLabel';
    component.plotlabelXAxis = 'mockButValidXAxis';
    component.plotlabelYAxis = 'mockButValidYAxis';
    component.currentChartType = 'line'
    expect(component.validPlotConfig).toBe(true)
  })

  it('should recognize invalid selections', () => {
    component.benchmarkName = '';
    component.commitsAndDevices = [];
    component.currentPlotTypeOption = {
      options: [],
      plottableAs: [],
      plotName: ''
    };
    component.plotlabelTitle = 'mockButValidLabel';
    component.plotlabelXAxis = 'mockButValidXAxis';
    component.plotlabelYAxis = 'mockButValidYAxis';
    component.currentChartType = 'line'
    expect(component.validPlotConfig).toBe(true)
  })

  // TODO implement tests for PlotConfigurationDialogComponent, the data binding properties of currentPlotOptions are very magical
});
