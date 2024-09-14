import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DependenciasFormComponent } from './dependencias-form.component';

describe('DependenciasFormComponent', () => {
  let component: DependenciasFormComponent;
  let fixture: ComponentFixture<DependenciasFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DependenciasFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DependenciasFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
