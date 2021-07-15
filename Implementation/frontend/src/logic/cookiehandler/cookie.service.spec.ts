import { TestBed } from '@angular/core/testing';

import { CookieService } from './cookie.service';
import {CookieModule} from "ngx-cookie";

describe('CookieService', () => {
  let service: CookieService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        CookieModule.forChild(),
      ]
    });
    service = TestBed.inject(CookieService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
