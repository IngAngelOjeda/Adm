import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificarClaveComponent } from './modificar-clave.component';

describe('ModificarClaveComponent', () => {
  let component: ModificarClaveComponent;
  let fixture: ComponentFixture<ModificarClaveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModificarClaveComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModificarClaveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
