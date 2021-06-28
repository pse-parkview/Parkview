import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideBenchmarkCompareComponent } from './side-benchmark-compare.component';

describe('SideBenchmarkCompareComponent', () => {
  let component: SideBenchmarkCompareComponent;
  let fixture: ComponentFixture<SideBenchmarkCompareComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SideBenchmarkCompareComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideBenchmarkCompareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
