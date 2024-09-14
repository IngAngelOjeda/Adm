import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganigramaTicVisorComponent } from './organigrama-tic-visor.component';

describe('OrganigramaTicVisorComponent', () => {
  let component: OrganigramaTicVisorComponent;
  let fixture: ComponentFixture<OrganigramaTicVisorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrganigramaTicVisorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganigramaTicVisorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
