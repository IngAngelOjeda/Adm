import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisorOrganigramaFormComponent } from './visor-organigrama-form.component';

describe('VisorOrganigramaFormComponent', () => {
  let component: VisorOrganigramaFormComponent;
  let fixture: ComponentFixture<VisorOrganigramaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VisorOrganigramaFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VisorOrganigramaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
