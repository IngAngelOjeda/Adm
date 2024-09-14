import { TestBed } from '@angular/core/testing';

import { OrganigramaTicService } from './organigrama-tic.service';

describe('OrganigramaTicService', () => {
  let service: OrganigramaTicService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganigramaTicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
