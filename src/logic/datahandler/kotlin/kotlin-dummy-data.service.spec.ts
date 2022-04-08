import { TestBed } from '@angular/core/testing';

import { KotlinDummyDataService } from './kotlin-dummy-data.service';

describe('KotlinDummyDataService', () => {
  let service: KotlinDummyDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KotlinDummyDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
