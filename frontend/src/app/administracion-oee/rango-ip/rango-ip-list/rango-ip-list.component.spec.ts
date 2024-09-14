import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RangoIpListComponent } from './rango-ip-list.component';

describe('RangoIpListComponent', () => {
  let component: RangoIpListComponent;
  let fixture: ComponentFixture<RangoIpListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RangoIpListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RangoIpListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
