import {Injectable} from '@angular/core';
import {Commit} from "../datahandler/interfaces/commit";
import {Pair} from "./interfaces/pair";
import {SelectedCommits} from "./interfaces/selected-commits";

@Injectable({
  providedIn: 'root'
})
export class CommitSelectionService {

  private selectedCommits: SelectedCommits = {
    benchmarkName: "",
    commitsAndDevices: [],
  };

  constructor() {
  }

  updateBenchmarkName(benchmarkName: string) {
    if (this.selectedCommits.benchmarkName === benchmarkName) {
      return;
    }
    this.selectedCommits = {
      benchmarkName: benchmarkName,
      commitsAndDevices: [],
    };
  }

  addCommit(benchmarkName: string, commit: Commit, device: string) {
    if (this.selectedCommits.benchmarkName === benchmarkName) {
      if (!this.selectedCommits.commitsAndDevices.some(c => c.commit === commit && c.device === device)) {
        this.selectedCommits.commitsAndDevices.push({commit, device});
      }
    } else {
      this.selectedCommits = {
        benchmarkName: benchmarkName,
        commitsAndDevices: [
          {commit, device},
        ],
      }
    }
  }

  removeCommit(commit: Commit, device: string) {
    const pairFilterOutPredicate = (p: Pair) => !(p.commit.sha === commit.sha && p.device === device);
    this.selectedCommits.commitsAndDevices = this.selectedCommits.commitsAndDevices.filter(pairFilterOutPredicate);
  }

  getSelectedCommits() {
    return this.selectedCommits;
  }
}
