import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditoriaDetailsComponent } from './auditoria-details.component';

describe('AuditoriaDetailsComponent', () => {
  let component: AuditoriaDetailsComponent;
  let fixture: ComponentFixture<AuditoriaDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AuditoriaDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuditoriaDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
