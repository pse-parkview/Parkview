import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SidePreviousPlotsComponent} from './side-previous-plots.component';
import {MatDialogModule} from "@angular/material/dialog";
import {CookieModule} from "ngx-cookie";

describe('SidePreviousPlotsComponent', () => {
  let component: SidePreviousPlotsComponent;
  let fixture: ComponentFixture<SidePreviousPlotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SidePreviousPlotsComponent
      ],
      imports: [
        MatDialogModule,
        CookieModule.forRoot(),
      ]
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
