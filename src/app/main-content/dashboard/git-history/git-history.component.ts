import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Commit} from "../../../../logic/datahandler/interfaces/commit";
import {SelectionService} from "../../../../logic/commit-selection-handler/selection.service";
import {CookieService} from "../../../../logic/cookiehandler/cookie.service";
import {RecentGitHistorySettings} from "../../../../logic/cookiehandler/interfaces/recent-git-history-settings";
import {ParkviewLibDataService} from "../../../../logic/datahandler/kotlin/parkview-lib-data.service";

interface CommitBundle {
  benchmarkedCommit?: Commit,
  nonBenchmarkedCommits?: Commit[],
}


@Component({
  selector: 'app-git-history',
  templateUrl: './git-history.component.html',
  styleUrls: ['./git-history.component.scss'],
})
export class GitHistoryComponent implements OnInit, AfterViewInit {
  currentlySelectedBranch: string = '';
  branchNames: string[] = [];
  currentlySelectedBenchmarkName: string = '';
  benchmarkNames: string[] = [];
  hideUnusableCommits: boolean = false;
  collapseUnusableCommits: boolean = false;
  maxNumberOfNonBenchmarkedCommits: number = 100;
  updateStep: number = 50;
  @ViewChild('anchor') anchor!: ElementRef<HTMLElement>;

  private bottomVisible: boolean = false;

  private observer!: IntersectionObserver;

  commits: CommitBundle[] = [];
  commitIterator!: Iterator<Commit>;
  selected: { commit: Commit, device: string }[] = [];

  constructor(private readonly dataService: ParkviewLibDataService,
              private readonly commitService: SelectionService,
              private readonly cookieService: CookieService) {
  }

  ngOnInit(): void {
    const lastSettings: RecentGitHistorySettings = this.cookieService.getMostRecentGitHistorySettings();
    this.hideUnusableCommits = lastSettings.hideUnusableCommits;
    this.dataService.getBenchmarks().subscribe((receivedBenchmarkNames: string[]) => {
      this.benchmarkNames = receivedBenchmarkNames;
      if (this.benchmarkNames.includes(lastSettings.benchmarkType)) {
        this.currentlySelectedBenchmarkName = lastSettings.benchmarkType;
      } else {
        this.currentlySelectedBenchmarkName = this.benchmarkNames.length > 0 ? this.benchmarkNames[0] : '';
      }
      this.updateCommitHistory();
    });
    this.dataService.getBranchNames().subscribe((receivedBranchNames: string[]) => {
      this.branchNames = receivedBranchNames;
      if (this.branchNames.includes(lastSettings.branch)) {
        this.currentlySelectedBranch = lastSettings.branch;
      } else {
        this.currentlySelectedBranch = this.branchNames.length > 0 ? this.branchNames[0] : '';
      }
      this.updateCommitHistory();
    });

    const options = {
      root: null,
      threshold: 0,
    };

    this.observer = new IntersectionObserver(([entry]) => {
      this.bottomVisible = entry.isIntersecting;
      this.onScroll();
    }, options);
  }

  ngAfterViewInit() {
    this.observer.observe(this.anchor.nativeElement);
  }

  updateCommitHistory(): void {
    if (this.currentlySelectedBranch.trim() === '' || this.currentlySelectedBenchmarkName.trim() === '') {
      return;
    }
    this.commitService.updateBenchmarkName(this.currentlySelectedBenchmarkName);
    this.commitService.updateBranchName(this.currentlySelectedBranch);
    this.commitIterator = this.dataService.getCommitHistory(this.currentlySelectedBranch, this.currentlySelectedBenchmarkName);
    this.commits = [];
    this.selected = [];
  }

  selectBranch(branchChoice: string): void {
    this.currentlySelectedBranch = branchChoice;
    this.cookieService.saveGitHistoryBranch(branchChoice);
    this.updateCommitHistory();
  }

  selectBenchmarkName(benchmarkNameChoice: string): void {
    this.currentlySelectedBenchmarkName = benchmarkNameChoice;
    this.cookieService.saveGitHistoryBenchmarkType(benchmarkNameChoice);
    this.updateCommitHistory();
  }

  toggleHideUnusableCommits(): void {
    this.hideUnusableCommits = !this.hideUnusableCommits;
    this.cookieService.saveGitHistoryHideUnusableCommits(this.hideUnusableCommits);
  }

  toggleCollapseUnusableCommits(): void {
    this.collapseUnusableCommits = !this.collapseUnusableCommits;
    // TODO: Cookie handler for collapse commits
    // this.cookieService.saveGitHistoryHideUnusableCommits(this.hideUnusableCommits);
  }

  selectCommit(commit: Commit) {
    for (let s of this.selected) {
      if (s.commit.sha === commit.sha) {
        this.commitService.addCommit(this.currentlySelectedBenchmarkName, s.commit, s.device);
      }
    }
  }

  selectDevice(commit: Commit, device: string, checked: boolean) {
    if (checked) {
      if (this.selected.filter(c => c.commit.sha === commit.sha && c.device === device).length === 0) {
        this.selected.push({commit, device});
      }
    } else { // not checked
      for (let i = 0; i < this.selected.length; i++) {
        if (this.selected[i].commit.sha === commit.sha && this.selected[i].device === device) {
          this.selected.splice(i, 1);
        }
      }
    }
  }

  onScroll(force: boolean = false) {
    let last = this.commits.length > 0 ? this.commits[this.commits.length - 1] : undefined;
    if (!force) {
      if (!this.bottomVisible) return;
      if (last && last.nonBenchmarkedCommits && last.nonBenchmarkedCommits.length >= this.maxNumberOfNonBenchmarkedCommits && this.collapseUnusableCommits) return;
    }

    let noBenchmark = []
    for (let i = 0; i < this.updateStep; ++i) {
      let n = this.commitIterator.next();
      if (n.done) break;
      if (n.value.availableDevices.length > 0) {
        let last = this.commits.length > 0 ? this.commits[this.commits.length - 1] : undefined;
        if (last && last.nonBenchmarkedCommits) {
          last.nonBenchmarkedCommits = last.nonBenchmarkedCommits.concat(noBenchmark);
        } else {
          this.commits.push({nonBenchmarkedCommits: noBenchmark});
        }
        this.commits.push({benchmarkedCommit: n.value});
        noBenchmark = [];
        continue;
      }
      noBenchmark.push(n.value);
    }

    last = this.commits.length > 0 ? this.commits[this.commits.length - 1] : undefined;
    if (noBenchmark.length > 0) {
      if (last && last.nonBenchmarkedCommits) {
        last.nonBenchmarkedCommits = last.nonBenchmarkedCommits.concat(noBenchmark);
      } else {
        this.commits.push({nonBenchmarkedCommits: noBenchmark});
      }
    }

    // this.onScroll();
  }
}
