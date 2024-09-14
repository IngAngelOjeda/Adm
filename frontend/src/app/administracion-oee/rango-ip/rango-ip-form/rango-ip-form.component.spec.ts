import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RangoIpFormComponent } from './rango-ip-form.component';

describe('RangoIpFormComponent', () => {
  let component: RangoIpFormComponent;
  let fixture: ComponentFixture<RangoIpFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RangoIpFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RangoIpFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
