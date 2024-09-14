import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformacionListComponent } from './informacion-list.component';

describe('InformacionListComponent', () => {
  let component: InformacionListComponent;
  let fixture: ComponentFixture<InformacionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InformacionListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InformacionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
