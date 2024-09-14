import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformacionDependenciasFormComponent } from './informacion-dependencias-form.component';

describe('InformacionDependenciasFormComponent', () => {
  let component: InformacionDependenciasFormComponent;
  let fixture: ComponentFixture<InformacionDependenciasFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InformacionDependenciasFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InformacionDependenciasFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
