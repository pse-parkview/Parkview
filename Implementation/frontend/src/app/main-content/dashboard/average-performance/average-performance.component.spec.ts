import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AveragePerformanceComponent } from './average-performance.component';

describe('AveragePerformanceComponent', () => {
  let component: AveragePerformanceComponent;
  let fixture: ComponentFixture<AveragePerformanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AveragePerformanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AveragePerformanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
