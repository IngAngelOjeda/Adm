import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubdominioFormComponent } from './subdominio-form.component';

describe('SubdominioFormComponent', () => {
  let component: SubdominioFormComponent;
  let fixture: ComponentFixture<SubdominioFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubdominioFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubdominioFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
