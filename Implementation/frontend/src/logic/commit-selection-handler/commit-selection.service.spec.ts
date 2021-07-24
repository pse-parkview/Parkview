import { TestBed } from '@angular/core/testing';

import { SelectionService } from './selection.service';

describe('CommitService', () => {
  let service: SelectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SelectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
