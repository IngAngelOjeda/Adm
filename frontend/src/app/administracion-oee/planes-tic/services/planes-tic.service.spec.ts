import { TestBed } from '@angular/core/testing';

import { PlanesTicService } from './planes-tic.service';

describe('PlanesTicService', () => {
  let service: PlanesTicService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlanesTicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  })
});