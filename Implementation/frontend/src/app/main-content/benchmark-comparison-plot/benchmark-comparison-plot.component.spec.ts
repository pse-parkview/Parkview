import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkComparisonPlotComponent } from './benchmark-comparison-plot.component';
import {RouterTestingModule} from "@angular/router/testing";

describe('BenchmarkComparisonPlotComponent', () => {
  let component: BenchmarkComparisonPlotComponent;
  let fixture: ComponentFixture<BenchmarkComparisonPlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        BenchmarkComparisonPlotComponent
      ],
      imports: [
        RouterTestingModule,
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BenchmarkComparisonPlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
