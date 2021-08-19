import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SummaryCardComponent} from './summary-card.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LibModule} from "../../lib.module";

describe('SummaryCardComponent', () => {
  let component: SummaryCardComponent;
  let fixture: ComponentFixture<SummaryCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SummaryCardComponent
      ],
      imports: [
        HttpClientTestingModule,
        LibModule,
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
