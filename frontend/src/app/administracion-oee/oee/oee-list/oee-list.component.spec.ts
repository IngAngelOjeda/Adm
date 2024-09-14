import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OeeListComponent } from './oee-list.component';

describe('OeeListComponent', () => {
  let component: OeeListComponent;
  let fixture: ComponentFixture<OeeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OeeListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OeeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
