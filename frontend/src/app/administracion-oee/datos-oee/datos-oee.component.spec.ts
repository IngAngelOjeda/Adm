import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatosOeeComponent } from './datos-oee.component';

describe('DatosOeeComponent', () => {
  let component: DatosOeeComponent;
  let fixture: ComponentFixture<DatosOeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatosOeeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatosOeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
