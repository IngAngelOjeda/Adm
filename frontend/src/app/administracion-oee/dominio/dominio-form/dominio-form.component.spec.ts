import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DominioFormComponent } from './dominio-form.component';

describe('DominioFormComponent', () => {
  let component: DominioFormComponent;
  let fixture: ComponentFixture<DominioFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DominioFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DominioFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
