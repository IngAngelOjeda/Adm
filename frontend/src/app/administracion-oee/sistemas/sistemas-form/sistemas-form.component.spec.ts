import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SistemasFormComponent } from './sistemas-form.component';

describe('SistemasFormComponent', () => {
  let component: SistemasFormComponent;
  let fixture: ComponentFixture<SistemasFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SistemasFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SistemasFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
