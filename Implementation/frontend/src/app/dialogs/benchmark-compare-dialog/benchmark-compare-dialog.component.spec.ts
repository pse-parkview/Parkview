import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkCompareDialogComponent } from './benchmark-compare-dialog.component';

describe('BenchmarkCompareDialogComponent', () => {
  let component: BenchmarkCompareDialogComponent;
  let fixture: ComponentFixture<BenchmarkCompareDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BenchmarkCompareDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BenchmarkCompareDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
