import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidePreviousPlotsComponent } from './side-previous-plots.component';

describe('SidePreviousPlotsComponent', () => {
  let component: SidePreviousPlotsComponent;
  let fixture: ComponentFixture<SidePreviousPlotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SidePreviousPlotsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidePreviousPlotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
