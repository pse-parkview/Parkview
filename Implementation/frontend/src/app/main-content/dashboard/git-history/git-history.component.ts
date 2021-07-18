import {Component, OnInit} from '@angular/core';
import {DataService} from "../../../../logic/datahandler/data.service";
import {Commit} from "../../../../logic/datahandler/interfaces/commit";
import {CommitSelectionService} from "../../../../logic/commit-selection-handler/commit-selection.service";

@Component({
  selector: 'app-git-history',
  templateUrl: './git-history.component.html',
  styleUrls: ['./git-history.component.scss']
})
export class GitHistoryComponent implements OnInit {

  // template instance variables
  currentlySelectedBranch: string = '';
  branchNames: string[] = [];
  currentlySelectedBenchmarkName: string = '';
  benchmarkNames: string[] = [];
  hideUnusableCommits: boolean = false;


  commits: Commit[] = [];
  selected: { commit: Commit, device: string }[] = [];

  constructor(private readonly dataService: DataService, private readonly commitService: CommitSelectionService) {
  }

  ngOnInit(): void {
    this.dataService.getBenchmarks().subscribe((receivedBenchmarkNames: string[]) => {
      this.benchmarkNames = receivedBenchmarkNames;
      this.currentlySelectedBenchmarkName = this.benchmarkNames.length > 0 ? this.benchmarkNames[0] : '';
      this.updateCommitHistory();
    });
    this.dataService.getBranchNames().subscribe((receivedBranchNames: string[]) => {
      this.branchNames = receivedBranchNames;
      this.currentlySelectedBranch = this.branchNames.length > 0 ? this.branchNames[0] : '';
      this.updateCommitHistory();
    });
  }

  updateCommitHistory(): void {
    if (this.currentlySelectedBranch.trim() === '' || this.currentlySelectedBenchmarkName.trim() === '') {
      return;
    }
    this.commitService.updateBenchmarkName(this.currentlySelectedBenchmarkName);
    this.dataService.getCommitHistory(this.currentlySelectedBranch, this.currentlySelectedBenchmarkName).subscribe((commits: Commit[]) => {
      this.commits = commits;
    });
  }

  selectBranch(branchChoice: string): void {
    this.currentlySelectedBranch = branchChoice;
    this.updateCommitHistory();
  }

  selectBenchmarkName(benchmarkNameChoice: string): void {
    this.currentlySelectedBenchmarkName = benchmarkNameChoice;
    this.updateCommitHistory();
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
}
