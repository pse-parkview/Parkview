import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigButtonsComponent } from './config-buttons.component';

describe('ConfigButtonsComponent', () => {
  let component: ConfigButtonsComponent;
  let fixture: ComponentFixture<ConfigButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigButtonsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
