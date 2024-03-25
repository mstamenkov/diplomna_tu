import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExucutionsComponent } from './exucutions.component';

describe('ExucutionsComponent', () => {
  let component: ExucutionsComponent;
  let fixture: ComponentFixture<ExucutionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExucutionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExucutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
