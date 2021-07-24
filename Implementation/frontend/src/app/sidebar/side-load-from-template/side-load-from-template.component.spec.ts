import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideLoadFromTemplateComponent } from './side-load-from-template.component';

describe('SideLoadFromTemplateComponent', () => {
  let component: SideLoadFromTemplateComponent;
  let fixture: ComponentFixture<SideLoadFromTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SideLoadFromTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideLoadFromTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
