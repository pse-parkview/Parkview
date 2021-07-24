import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadFromTemplateDialogComponent } from './load-from-template-dialog.component';

describe('LoadFromTemplateDialogComponent', () => {
  let component: LoadFromTemplateDialogComponent;
  let fixture: ComponentFixture<LoadFromTemplateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadFromTemplateDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadFromTemplateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
