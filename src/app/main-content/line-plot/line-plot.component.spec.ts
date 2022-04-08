import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LinePlotComponent} from './line-plot.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";

describe('LinePlotComponent', () => {
  let component: LinePlotComponent;
  let fixture: ComponentFixture<LinePlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        LinePlotComponent
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinePlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
