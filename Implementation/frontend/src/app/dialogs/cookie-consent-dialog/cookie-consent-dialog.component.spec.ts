import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookieConsentDialogComponent } from './cookie-consent-dialog.component';

describe('CookieConsentDialogComponent', () => {
  let component: CookieConsentDialogComponent;
  let fixture: ComponentFixture<CookieConsentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CookieConsentDialogComponent ]
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
