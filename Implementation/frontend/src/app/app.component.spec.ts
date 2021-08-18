import {ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {AppComponent} from './app.component';
import {MatDialogModule} from "@angular/material/dialog";
import {CookieModule} from "ngx-cookie";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {CookieService} from "../logic/cookiehandler/cookie.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let cookieService: CookieService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        MatDialogModule,
        CookieModule.forRoot(),
      ],
      declarations: [
        AppComponent,
      ],
      schemas: [
        CUSTOM_ELEMENTS_SCHEMA,
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    cookieService = fixture.debugElement.injector.get(CookieService);

    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'frontend'`, () => {
    expect(component.title).toEqual('frontend');
  });

  it('should ask for cookie settings if new', function () {
    const cookieServiceDialogSpy = spyOn(cookieService, "spawnConsentDialog");
    const cookieServiceConsentSpy = spyOn(cookieService, "hasDecidedConsent")
      .and.returnValue(false);
    component.ngOnInit();
    expect(cookieServiceDialogSpy).toHaveBeenCalled();
  });

  it('should not ask for cookie settings if old', function () {
    const cookieServiceDialogSpy = spyOn(cookieService, "spawnConsentDialog");
    const cookieServiceConsentSpy = spyOn(cookieService, "hasDecidedConsent")
      .and.returnValue(true);
    component.ngOnInit();
    expect(cookieServiceDialogSpy).not.toHaveBeenCalled();
  });

});
