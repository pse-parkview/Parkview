import { TestBed } from '@angular/core/testing';

import { CommitService } from './commit.service';

describe('CommitService', () => {
  let service: CommitService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommitService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
