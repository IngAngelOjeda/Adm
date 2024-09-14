import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SistemasListComponent } from './sistemas-list.component';

describe('SistemasListComponent', () => {
  let component: SistemasListComponent;
  let fixture: ComponentFixture<SistemasListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SistemasListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SistemasListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
