import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanesTicListComponent } from './planes-tic-list.component';

describe('PlanesTicListComponent', () => {
  let component: PlanesTicListComponent;
  let fixture: ComponentFixture<PlanesTicListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlanesTicListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlanesTicListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
