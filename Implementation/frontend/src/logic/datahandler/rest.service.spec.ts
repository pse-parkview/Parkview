import {TestBed} from '@angular/core/testing';

import {RestService} from './rest.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('RestService', () => {
  let service: RestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ]
    });
    service = TestBed.inject(RestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
