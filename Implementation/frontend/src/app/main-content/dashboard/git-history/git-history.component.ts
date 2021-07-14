import {Component, OnInit} from '@angular/core';
import {DataService} from "../../../../logic/datahandler/data.service";
import {Commit} from "../../../../logic/datahandler/interfaces/commit";

@Component({
  selector: 'app-git-history',
  templateUrl: './git-history.component.html',
  styleUrls: ['./git-history.component.scss']
})
export class GitHistoryComponent implements OnInit {

  currentlySelectedBranch: string = '';
  branchNames: string[] = [];

  commits: Commit[] = [];
  selectedCommits: Commit[] = [];

  constructor(private readonly dataService: DataService) {
  }

  ngOnInit(): void {
    this.dataService.getBranchNames().subscribe((receivedBranchNames: string[]) => {
      this.branchNames = receivedBranchNames;
      this.currentlySelectedBranch = this.branchNames.length > 0 ? this.branchNames[0] : 'No branches exist?';
      this.selectBranch(this.currentlySelectedBranch);
    });

    this.dataService.getBenchmarks().subscribe((d => d.forEach(o => console.log(o))))
  }

  selectBranch(branchChoice: string): void {
    this.currentlySelectedBranch = branchChoice;
    this.dataService.getCommitHistory(this.currentlySelectedBranch).subscribe((commits: Commit[]) => {
      this.commits = commits;
    });
  }

  selectCommit(commit: Commit) {
    this.selectedCommits.push(commit);
    alert(`Selected commit: ${commit.message}`);
  }

  selectDevice(commit: string, device: string, checked: boolean) {
    alert(`${checked ? 'Selected' : 'Unselected'} device ${device} of commit "${commit}"`)
  }


}
