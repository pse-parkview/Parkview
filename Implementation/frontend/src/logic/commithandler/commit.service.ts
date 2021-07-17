import {Injectable} from '@angular/core';
import {Commit} from "../datahandler/interfaces/commit";
import {Pair} from "./pair";

@Injectable({
  providedIn: 'root'
})
export class CommitService {

  private commits: Pair[] = [];

  constructor() {
  }

  addCommit(commit: Commit, device: string) {

    if (this.commits.filter(c => c.commit === commit && c.device === device).length === 0) {
      this.commits.push({commit, device});
    }

  }

  getCommits(): Pair[] {
    return this.commits;
  }
}
