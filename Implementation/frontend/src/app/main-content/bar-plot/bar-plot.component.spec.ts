import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarPlotComponent } from './bar-plot.component';

describe('BarPlotComponent', () => {
  let component: BarPlotComponent;
  let fixture: ComponentFixture<BarPlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarPlotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarPlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
