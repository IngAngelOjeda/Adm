import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DominioListComponent } from './dominio-list.component';

describe('DominioListComponent', () => {
  let component: DominioListComponent;
  let fixture: ComponentFixture<DominioListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DominioListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DominioListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
