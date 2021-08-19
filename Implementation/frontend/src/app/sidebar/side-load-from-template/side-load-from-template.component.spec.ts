import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SideLoadFromTemplateComponent} from './side-load-from-template.component';
import {RouterTestingModule} from "@angular/router/testing";
import {MatDialogModule} from "@angular/material/dialog";
import {CookieModule} from "ngx-cookie";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('SideLoadFromTemplateComponent', () => {
  let component: SideLoadFromTemplateComponent;
  let fixture: ComponentFixture<SideLoadFromTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SideLoadFromTemplateComponent
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatDialogModule,
        CookieModule.forRoot(),
      ],
      providers: [
        CookieService
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideLoadFromTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
