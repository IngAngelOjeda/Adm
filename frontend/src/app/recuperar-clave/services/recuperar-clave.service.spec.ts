import { TestBed } from '@angular/core/testing';

import { RecuperarClaveService } from './recuperar-clave.service';

describe('RecuperarClaveService', () => {
  let service: RecuperarClaveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecuperarClaveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});