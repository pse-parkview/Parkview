import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GitHistoryComponent } from './git-history.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('GitHistoryComponent', () => {
  let component: GitHistoryComponent;
  let fixture: ComponentFixture<GitHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        GitHistoryComponent,
      ],
      imports: [
        HttpClientTestingModule,
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GitHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
