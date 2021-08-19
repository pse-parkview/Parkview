import {ComponentFixture, TestBed} from '@angular/core/testing';
import {BarPlotComponent} from './bar-plot.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('BarPlotComponent', () => {
  let component: BarPlotComponent;
  let fixture: ComponentFixture<BarPlotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        BarPlotComponent
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarPlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
