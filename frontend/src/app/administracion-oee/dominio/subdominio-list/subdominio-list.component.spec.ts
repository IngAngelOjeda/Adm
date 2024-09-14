import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubdominioListComponent } from './subdominio-list.component';

describe('SubdominioListComponent', () => {
  let component: SubdominioListComponent;
  let fixture: ComponentFixture<SubdominioListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubdominioListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubdominioListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
