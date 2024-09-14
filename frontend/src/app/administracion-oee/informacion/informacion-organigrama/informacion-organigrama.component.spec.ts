import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformacionOrganigramaComponent } from './informacion-organigrama.component';

describe('InformacionOrganigramaComponent', () => {
  let component: InformacionOrganigramaComponent;
  let fixture: ComponentFixture<InformacionOrganigramaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InformacionOrganigramaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InformacionOrganigramaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
