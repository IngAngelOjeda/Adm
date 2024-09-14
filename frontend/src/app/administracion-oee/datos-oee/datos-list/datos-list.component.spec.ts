import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatosListComponent } from './datos-list.component';

describe('DatosListComponent', () => {
  let component: DatosListComponent;
  let fixture: ComponentFixture<DatosListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatosListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatosListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
