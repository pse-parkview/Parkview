import { TestBed } from '@angular/core/testing';

import { CookieService } from './cookie.service';
import {CookieModule} from "ngx-cookie";
import {MatDialogModule} from "@angular/material/dialog";

describe('CookieService', () => {
  let service: CookieService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        CookieModule.forChild(),
      ]
    });
    service = TestBed.inject(CookieService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
