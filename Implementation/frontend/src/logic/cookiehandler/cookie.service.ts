import {Injectable, EventEmitter} from '@angular/core';
import {PlotConfiguration} from "../plothandler/interfaces/plot-configuration";
import {MatDialog} from "@angular/material/dialog";
import {CookieConsentDialogComponent} from "../../app/dialogs/cookie-consent-dialog/cookie-consent-dialog.component";
import {CookieSettings} from "./interfaces/cookie-settings";
import {CookieService as NgxCookieService} from "ngx-cookie";
import {RecentGitHistorySettings} from "./interfaces/recent-git-history-settings";
import {Template} from "./interfaces/template";

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  public readonly recentPlotsUpdate = new EventEmitter<void>();
  public readonly savedTemplateUpdate = new EventEmitter<void>();

  private static readonly NAME_SETTINGS = 'settings';
  private static readonly NAME_RECENT_GIT_HISTORY_SETTINGS = 'recent_git_history_settings';
  private static readonly NAME_RECENT_PLOT_CONFIGS = 'recent_plot_configs';
  private static readonly NAME_SAVED_TEMPLATES = 'saved_plot_templates';

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

  public saveGitHistoryBenchmarkType(benchmarkType: string): void {
    const settings = this.getMostRecentGitHistorySettings();
    settings.benchmarkType = benchmarkType;
    this.ngxCookieService.putObject(CookieService.NAME_RECENT_GIT_HISTORY_SETTINGS, settings);
  }

  public saveGitHistoryBranch(branchName: string): void {
    const settings = this.getMostRecentGitHistorySettings();
    settings.branch = branchName;
    this.ngxCookieService.putObject(CookieService.NAME_RECENT_GIT_HISTORY_SETTINGS, settings);
  }

  public saveGitHistoryHideUnusableCommits(hide: boolean): void {
    const settings = this.getMostRecentGitHistorySettings();
    settings.hideUnusableCommits = hide;
    this.ngxCookieService.putObject(CookieService.NAME_RECENT_GIT_HISTORY_SETTINGS, settings);
  }

  public getMostRecentGitHistorySettings(): RecentGitHistorySettings {
    const settings = this.ngxCookieService.getObject(CookieService.NAME_RECENT_GIT_HISTORY_SETTINGS) as RecentGitHistorySettings;
    if (settings === undefined
      || settings.benchmarkType === undefined
      || settings.branch === undefined
      || settings.hideUnusableCommits === undefined) {
      return {
        branch: '',
        benchmarkType: '',
        hideUnusableCommits: false,
      };
    }
    return settings;
  }

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

  public addTemplate(plotConfig: PlotConfiguration): void {
    const savedConfigs: Template[] = this.getSavedTemplates();
    savedConfigs.push({
      date: new Date(),
      config: plotConfig,
    });
    this.ngxCookieService.putObject(CookieService.NAME_SAVED_TEMPLATES, savedConfigs);
    this.savedTemplateUpdate.emit();
  }

  public getSavedTemplates(): Template[] {
    const savedTemplates: Template[] = this.ngxCookieService.getObject(CookieService.NAME_SAVED_TEMPLATES) as Array<Template>;
    return savedTemplates ? savedTemplates : [];
  }

  public deleteTemplate(template: Template) {
    const savedConfigs: Template[] = this.getSavedTemplates();
    const newConfigs: Template[] = savedConfigs.filter(t => !CookieService.templateEquals(template, t));
    this.ngxCookieService.putObject(CookieService.NAME_SAVED_TEMPLATES, newConfigs);
    this.savedTemplateUpdate.emit();
  }

  private static templateEquals(t1: Template, t2: Template): boolean {
    if (t1.date != t2.date) {
      return false;
    }
    const everythingButOptions = t1.config.plotType === t2.config.plotType
      && t1.config.chartType === t2.config.chartType
      && JSON.stringify(t1.config.commits) === JSON.stringify(t2.config.commits)
      && JSON.stringify(t1.config.devices) === JSON.stringify(t2.config.devices)
      && t1.config.labelForTitle === t2.config.labelForTitle
      && t1.config.labelForXAxis === t2.config.labelForXAxis
      && t1.config.labelForYAxis === t2.config.labelForYAxis;
    if (!everythingButOptions) {
      return false;
    }
    const keys = Object.keys(t1.config.options)
    for (const k of keys) {
      if (t1.config.options[k] !== t2.config.options[k]) {
        return false;
      }
    }
    const keys2 = Object.keys(t2.config.options)
    for (const k of keys2) {
      if (t1.config.options[k] !== t2.config.options[k]) {
        return false;
      }
    }
    return true;
  }
}
