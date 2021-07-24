import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BenchmarkSelectDialogComponent } from './benchmark-select-dialog.component';

describe('BenchmarkSelectDialogComponent', () => {
  let component: BenchmarkSelectDialogComponent;
  let fixture: ComponentFixture<BenchmarkSelectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BenchmarkSelectDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BenchmarkSelectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
