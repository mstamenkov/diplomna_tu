import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogExecutionsComponent } from './dialog-executions.component';

describe('DialogExecutionsComponent', () => {
  let component: DialogExecutionsComponent;
  let fixture: ComponentFixture<DialogExecutionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogExecutionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogExecutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
