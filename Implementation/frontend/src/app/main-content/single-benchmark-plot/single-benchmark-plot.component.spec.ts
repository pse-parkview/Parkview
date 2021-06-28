import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleBenchmarkPlotComponent } from './single-benchmark-plot.component';

describe('SingleBenchmarkPlotComponent', () => {
  let component: SingleBenchmarkPlotComponent;
  let fixture: ComponentFixture<SingleBenchmarkPlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SingleBenchmarkPlotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleBenchmarkPlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
