import { TestBed } from '@angular/core/testing';

import { ClasificadorService } from './clasificador.service';

describe('PermisoService', () => {
  let service: ClasificadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClasificadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
