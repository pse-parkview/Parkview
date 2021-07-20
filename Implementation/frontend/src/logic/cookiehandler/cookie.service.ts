import { Injectable } from '@angular/core';
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {MatDialog} from "@angular/material/dialog";
import {CookieConsentDialogComponent} from "../../app/dialogs/cookie-consent-dialog/cookie-consent-dialog.component";
import {CookieSettings} from "./interfaces/cookie-settings";
import {CookieService as NgxCookieService} from "ngx-cookie";
import {CookieLastBranch} from "./interfaces/cookie-last-branch";

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  private static readonly NAME_SETTINGS = 'settings';
  private static readonly NAME_RECENT_BRANCH = 'recent_branch';

  constructor(private readonly dialog: MatDialog,
              private readonly ngcCookieService: NgxCookieService,
              ) {
  }

  public spawnConsentDialog() {
    this.dialog.open(CookieConsentDialogComponent, {
      disableClose: true,
    });
  }

  public hasDecidedConsent(): boolean {
    const settings: CookieSettings = this.ngcCookieService.getObject(CookieService.NAME_SETTINGS) as CookieSettings;
    return settings != undefined;
  }

  public saveSettings(settings: CookieSettings) {
    this.ngcCookieService.putObject(CookieService.NAME_SETTINGS, settings);
  }

  public getMostRecentBranch(): string | null {
    const branchName: CookieLastBranch = this.ngcCookieService.getObject(CookieService.NAME_RECENT_BRANCH) as CookieLastBranch;
    return branchName ? branchName.name : null;
  }

  public saveMostRecentBranch(branchName: string) {
    this.ngcCookieService.putObject(CookieService.NAME_RECENT_BRANCH, {name: branchName});
  }

  public getRecentPlotConfigurations(): PlotConfiguration[] {
    return [];
  }

}
