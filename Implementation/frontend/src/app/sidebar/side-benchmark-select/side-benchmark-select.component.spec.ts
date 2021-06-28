import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideBenchmarkSelectComponent } from './side-benchmark-select.component';

describe('SideBenchmarkSelectComponent', () => {
  let component: SideBenchmarkSelectComponent;
  let fixture: ComponentFixture<SideBenchmarkSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SideBenchmarkSelectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideBenchmarkSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
