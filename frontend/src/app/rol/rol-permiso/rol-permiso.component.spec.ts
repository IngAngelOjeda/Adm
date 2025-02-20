import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolPermisoComponent } from './rol-permiso.component';

describe('RolPermisoComponent', () => {
  let component: RolPermisoComponent;
  let fixture: ComponentFixture<RolPermisoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RolPermisoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RolPermisoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
