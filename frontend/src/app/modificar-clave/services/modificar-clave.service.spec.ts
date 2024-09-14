import { TestBed } from '@angular/core/testing';

import { ModificarClaveService } from './modificar-clave.service';

describe('ModificarClaveService', () => {
  let service: ModificarClaveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModificarClaveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});