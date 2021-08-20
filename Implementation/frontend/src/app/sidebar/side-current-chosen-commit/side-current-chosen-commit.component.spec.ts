import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SideCurrentChosenCommitComponent} from './side-current-chosen-commit.component';
import {MatCardModule} from "@angular/material/card";
import {MatDialogModule} from "@angular/material/dialog";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {LibModule} from "../../../lib/lib.module";

describe('SideCurrentChosenCommitComponent', () => {
  let component: SideCurrentChosenCommitComponent;
  let fixture: ComponentFixture<SideCurrentChosenCommitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SideCurrentChosenCommitComponent
      ],
      imports: [
        MatCardModule,
        MatDialogModule,
        MatSnackBarModule,
        LibModule,
      ],
      providers: []
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
