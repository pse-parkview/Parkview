import { TestBed } from '@angular/core/testing';

import { PlotService } from './plot.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('PlotService', () => {
  let service: PlotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(PlotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
