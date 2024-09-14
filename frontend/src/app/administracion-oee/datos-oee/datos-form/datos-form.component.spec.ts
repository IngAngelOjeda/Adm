import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatosFormComponent } from './datos-form.component';

describe('DatosFormComponent', () => {
  let component: DatosFormComponent;
  let fixture: ComponentFixture<DatosFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatosFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatosFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
