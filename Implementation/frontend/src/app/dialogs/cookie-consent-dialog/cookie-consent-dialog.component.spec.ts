import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookieConsentDialogComponent } from './cookie-consent-dialog.component';
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {COOKIE_OPTIONS, CookieModule, CookieOptionsProvider, CookieService} from "ngx-cookie";

describe('CookieConsentDialogComponent', () => {
  let component: CookieConsentDialogComponent;
  let fixture: ComponentFixture<CookieConsentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CookieConsentDialogComponent
      ],
      imports: [
        MatDialogModule,
        CookieModule.forChild(),
      ],
      providers: [
        {provide: MatDialogRef, useValue: {}},
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CookieConsentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
