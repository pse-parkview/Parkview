import {Component, OnInit} from '@angular/core';
import {DataService} from "../../../../logic/datahandler/data.service";
import {Commit} from "../../../../logic/datahandler/interfaces/commit";

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
  selectedCommits: Commit[] = [];

  constructor(private readonly dataService: DataService) {
  }

  ngOnInit(): void {
    this.dataService.getBenchmarks().subscribe((receivedBenchmarkNames: string[]) => {
      this.benchmarkNames = receivedBenchmarkNames;
      this.currentlySelectedBenchmarkName = this.benchmarkNames.length > 0 ? this.benchmarkNames[0] : '';
      this.updateCommitHistory();
    })
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
    this.selectedCommits.push(commit);
    alert(`Selected commit: ${commit.message}`);
  }

  selectDevice(commit: string, device: string, checked: boolean) {
    alert(`${checked ? 'Selected' : 'Unselected'} device ${device} of commit "${commit}"`)
  }
}
