import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DependenciasListComponent } from './dependencias-list.component';

describe('DependenciasListComponent', () => {
  let component: DependenciasListComponent;
  let fixture: ComponentFixture<DependenciasListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DependenciasListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DependenciasListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
