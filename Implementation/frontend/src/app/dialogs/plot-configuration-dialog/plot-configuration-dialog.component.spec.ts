import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlotConfigurationDialogComponent } from './plot-configuration-dialog.component';

describe('PlotConfigurationDialogComponent', () => {
  let component: PlotConfigurationDialogComponent;
  let fixture: ComponentFixture<PlotConfigurationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlotConfigurationDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlotConfigurationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
