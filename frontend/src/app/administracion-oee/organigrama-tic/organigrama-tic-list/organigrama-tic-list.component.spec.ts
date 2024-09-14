import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganigramaTicListComponent } from './organigrama-tic-list.component';

describe('OrganigramaTicListComponent', () => {
  let component: OrganigramaTicListComponent;
  let fixture: ComponentFixture<OrganigramaTicListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrganigramaTicListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganigramaTicListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
