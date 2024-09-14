import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformacionDependenciasListComponent } from './informacion-dependencias-list.component';

describe('InformacionDependenciasListComponent', () => {
  let component: InformacionDependenciasListComponent;
  let fixture: ComponentFixture<InformacionDependenciasListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InformacionDependenciasListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InformacionDependenciasListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
