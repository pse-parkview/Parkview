import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideCurrentChosenCommitComponent } from './side-current-chosen-commit.component';

describe('SideCurrentChosenCommitComponent', () => {
  let component: SideCurrentChosenCommitComponent;
  let fixture: ComponentFixture<SideCurrentChosenCommitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SideCurrentChosenCommitComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideCurrentChosenCommitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
