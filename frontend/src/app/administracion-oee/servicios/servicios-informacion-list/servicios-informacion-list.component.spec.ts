import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiciosInformacionListComponent } from './servicios-informacion-list.component';

describe('ServiciosInformacionComponent', () => {
  let component: ServiciosInformacionListComponent;
  let fixture: ComponentFixture<ServiciosInformacionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiciosInformacionListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiciosInformacionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
