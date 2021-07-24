import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {MatSlideToggleChange} from "@angular/material/slide-toggle";
import {CookieService} from "../../../logic/cookiehandler/cookie.service";
import {CookieSettings} from "../../../logic/cookiehandler/interfaces/cookie-settings";

@Component({
  selector: 'app-cookie-consent-dialog',
  templateUrl: './cookie-consent-dialog.component.html',
  styleUrls: ['./cookie-consent-dialog.component.scss']
})
export class CookieConsentDialogComponent implements OnInit {

  cookies_storing_plot_configs: boolean = true;

  constructor(private readonly matDialog: MatDialogRef<CookieConsentDialogComponent>,
              private readonly cookieService: CookieService) {
  }

  ngOnInit(): void {
    // no initialisation yet
  }

  toggleStoringPlotConfigs(event: MatSlideToggleChange) {
    this.cookies_storing_plot_configs = event.checked
    console.log(this.cookies_storing_plot_configs)
  }

  saveSettings() {
    console.log("YOO SAVING COOKIES AND STORING PLOT CONFIGS: ", this.cookies_storing_plot_configs)
    const settings: CookieSettings = {
      storingPlotConfigs: this.cookies_storing_plot_configs
    }
    this.cookieService.saveSettings(settings)
  }

}
