import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  @Output()
  sideBarToggle: EventEmitter<void> = new EventEmitter<void>();


  constructor() {
    // do nothing
  }

  ngOnInit(): void {
  }

  navToGithub(): void {
    window.location.href = "https://github.com/pse-parkview/PSE_dashboard";
  }
}
