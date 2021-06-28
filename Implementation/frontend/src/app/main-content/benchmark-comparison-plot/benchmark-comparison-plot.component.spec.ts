import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkComparisonPlotComponent } from './benchmark-comparison-plot.component';

describe('BenchmarkComparisonPlotComponent', () => {
  let component: BenchmarkComparisonPlotComponent;
  let fixture: ComponentFixture<BenchmarkComparisonPlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BenchmarkComparisonPlotComponent ]
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
