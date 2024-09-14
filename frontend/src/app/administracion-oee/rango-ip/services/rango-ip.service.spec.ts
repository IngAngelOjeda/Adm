import { TestBed } from '@angular/core/testing';

import { RangoIpService } from './rango-ip.service';

describe('RangoIpService', () => {
  let service: RangoIpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RangoIpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  })
});