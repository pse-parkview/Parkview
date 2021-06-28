import { TestBed } from '@angular/core/testing';

import { PlotService } from './plot.service';

describe('PlotService', () => {
  let service: PlotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
