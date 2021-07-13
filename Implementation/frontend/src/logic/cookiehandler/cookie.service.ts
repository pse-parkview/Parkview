import { Injectable } from '@angular/core';
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {MatDialog} from "@angular/material/dialog";
import {CookieConsentDialogComponent} from "../../app/dialogs/cookie-consent-dialog/cookie-consent-dialog.component";
import {CookieSettings} from "./interfaces/cookie-settings";
import {CookieService as NgxCookieService} from "ngx-cookie";

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  private static readonly SETTINGS_NAME = 'settings';

  constructor(private readonly dialog: MatDialog,
              private readonly ngcCookieService: NgxCookieService,
              ) {
  }

  public spawnConsentDialog() {
    this.dialog.open(CookieConsentDialogComponent);
  }

  public hasDecidedConsent(): boolean {
    const settings: CookieSettings = this.ngcCookieService.getObject(CookieService.SETTINGS_NAME) as CookieSettings;
    return settings != undefined;
  }

  public saveSettings(settings: CookieSettings) {
    this.ngcCookieService.putObject(CookieService.SETTINGS_NAME, settings);
  }

  public getRecentPlotConfigurations(): PlotConfiguration[] {
    return [];
  }

}
