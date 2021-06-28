import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryChartComponent } from './summary-chart.component';

describe('SummaryChartComponent', () => {
  let component: SummaryChartComponent;
  let fixture: ComponentFixture<SummaryChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
