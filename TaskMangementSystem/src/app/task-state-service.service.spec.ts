import { TestBed } from '@angular/core/testing';

import { TaskStateServiceService } from './task-state-service.service';

describe('TaskStateServiceService', () => {
  let service: TaskStateServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskStateServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
