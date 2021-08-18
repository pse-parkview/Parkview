import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {CookieService} from "../logic/cookiehandler/cookie.service";
import {MatDrawerMode, MatSidenav} from "@angular/material/sidenav";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, AfterViewInit {
  readonly title = 'frontend';
  sideNavMode: MatDrawerMode = 'side';

  @ViewChild(MatSidenav)
  private sideNav: MatSidenav = {} as MatSidenav;

  constructor(private readonly cookieService: CookieService) {
  }

  ngOnInit(): void {
    if (!this.cookieService.hasDecidedConsent()) {
      this.cookieService.spawnConsentDialog();
    }
    this.changeNavModes();
  }

  ngAfterViewInit() {
    window.addEventListener('resize', () => this.changeNavModes());
  }

  toggleSideNav(): void {
    this.sideNav.toggle();
  }

  private changeNavModes(): void {
    if (AppComponent.isVertical()) {
      this.sideNavMode = 'over';
    } else {
      this.sideNavMode = 'side';
    }
  }

  private static isVertical(): boolean {
    return document.body.clientHeight > document.body.clientWidth;
  }
}
