import { TestBed } from '@angular/core/testing';

import { DominioService } from './dominio.service';
import { SubdominioService } from './subdominio.service';

describe('DominioService', () => {
  let dominioService: DominioService;
  let subdominioService: SubdominioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SubdominioService] 
    });

    dominioService = TestBed.inject(DominioService);
    subdominioService = TestBed.inject(SubdominioService);
  });

  it('should be created', () => {
    expect(dominioService).toBeTruthy();
    expect(subdominioService).toBeTruthy();
  });

});

