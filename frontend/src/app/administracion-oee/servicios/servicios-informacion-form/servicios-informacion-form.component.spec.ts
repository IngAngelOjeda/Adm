import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiciosInformacionFormComponent } from './servicios-informacion-form.component';

describe('ServiciosInformacionFormComponent', () => {
  let component: ServiciosInformacionFormComponent;
  let fixture: ComponentFixture<ServiciosInformacionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiciosInformacionFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiciosInformacionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
