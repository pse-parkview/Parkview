import {Injectable, EventEmitter} from '@angular/core';
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {MatDialog} from "@angular/material/dialog";
import {CookieConsentDialogComponent} from "../../app/dialogs/cookie-consent-dialog/cookie-consent-dialog.component";
import {CookieSettings} from "./interfaces/cookie-settings";
import {CookieService as NgxCookieService} from "ngx-cookie";

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  public readonly recentPlotsUpdate = new EventEmitter<void>();

  private static readonly NAME_SETTINGS = 'settings';
  private static readonly NAME_RECENT_BRANCH = 'recent_branch';
  private static readonly NAME_RECENT_PLOT_CONFIGS = 'recent_plot_configs';

  constructor(private readonly dialog: MatDialog,
              private readonly ngxCookieService: NgxCookieService,
              ) {
  }

  public spawnConsentDialog() {
    this.dialog.open(CookieConsentDialogComponent, {
      disableClose: true,
    });
  }

  public hasDecidedConsent(): boolean {
    const settings: CookieSettings = this.ngxCookieService.getObject(CookieService.NAME_SETTINGS) as CookieSettings;
    return settings != undefined;
  }

  public saveSettings(settings: CookieSettings) {
    this.ngxCookieService.putObject(CookieService.NAME_SETTINGS, settings);
  }

  public getMostRecentBranch(): string | null {
    const branchName: string = this.ngxCookieService.get(CookieService.NAME_RECENT_BRANCH);
    return branchName ? branchName : null;
  }

  public saveMostRecentBranch(branchName: string) {
    this.ngxCookieService.put(CookieService.NAME_RECENT_BRANCH, branchName);
  }

  // TODO connect plot configuration cookies to places that are concerned
  public addRecentPlotConfiguration(plotConfig: PlotConfiguration): void {
    let recentConfigs: PlotConfiguration[] = this.ngxCookieService.getObject(CookieService.NAME_RECENT_PLOT_CONFIGS) as Array<PlotConfiguration>;
    if (recentConfigs !== undefined) {
      recentConfigs.unshift(plotConfig);
      if (recentConfigs.length > 5) {
        recentConfigs.pop();
      }
    } else {
      recentConfigs = [ plotConfig ];
    }
    this.ngxCookieService.putObject(CookieService.NAME_RECENT_PLOT_CONFIGS, recentConfigs);
    this.recentPlotsUpdate.emit();
  }

  public getRecentPlotConfigurations(): PlotConfiguration[] {
    const recentConfigs: PlotConfiguration[] = this.ngxCookieService.getObject(CookieService.NAME_RECENT_PLOT_CONFIGS) as Array<PlotConfiguration>;
    return recentConfigs ? recentConfigs : [];
  }

}
