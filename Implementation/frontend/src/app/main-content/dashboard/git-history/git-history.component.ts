import { Component, OnInit } from '@angular/core';
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


  private readonly mockCommits: Commit[] = [
    {
      date: new Date(),
      commitMessage: 'commit message lets go',
      author: 'max',
      hasBenchmark: true,
      sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
      branch: 'branchtest'
    },
    {
      date: new Date(),
      commitMessage: 'commit message 2 lets go',
      author: 'bongun',
      hasBenchmark: true,
      sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
      branch: 'branchtest'
    },
    {
      date: new Date(),
      commitMessage: 'commit message 3 lets go',
      author: 'darius',
      hasBenchmark: true,
      sha: 'shastringbrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr',
      branch: 'branchtest'
    },
  ]

  constructor(private readonly dataService: DataService) {
  }

  ngOnInit(): void {
    this.branchNames = this.dataService.getBranchNames();
    this.currentlySelectedBranch = this.branchNames.length > 0 ? this.branchNames[0] : 'No branches exist?';
  }

  selectBranch(branchChoice: string): void {
    console.log(branchChoice)
    this.currentlySelectedBranch = branchChoice;
    this.commits = this.mockCommits;
    //= this.dataService.getCommitHistory(this.currentlySelectedBranch);
  }

}
