import {Component, OnInit} from '@angular/core';
import {CookieService} from "../logic/cookiehandler/cookie.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'frontend';

  constructor(private readonly cookieService: CookieService) {
  }

  ngOnInit(): void {
    if (!this.cookieService.hasDecidedConsent()) {
      this.cookieService.spawnConsentDialog();
    }
  }
}
