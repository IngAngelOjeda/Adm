import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanesTicFormComponent } from './planes-tic-form.component';

describe('PlanesTicFormComponent', () => {
  let component: PlanesTicFormComponent;
  let fixture: ComponentFixture<PlanesTicFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlanesTicFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlanesTicFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
