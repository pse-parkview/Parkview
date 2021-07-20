import { TestBed } from '@angular/core/testing';

import { CommitSelectionService } from './commit-selection.service';

describe('CommitService', () => {
  let service: CommitSelectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommitSelectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
