import {EventEmitter, Injectable} from '@angular/core';
import {Commit} from "../datahandler/interfaces/commit";
import {Pair} from "./interfaces/pair";
import {SelectedCommits} from "./interfaces/selected-commits";

@Injectable({
  providedIn: 'root'
})
export class SelectionService {

  public readonly selectedCommitsHasUpdated: EventEmitter<void> = new EventEmitter<void>();
  public readonly selectedBranchHasUpdated: EventEmitter<void> = new EventEmitter<void>();

  private selectedCommits: SelectedCommits = {
    benchmarkName: "",
    commitsAndDevices: [],
  }

  private selectedBranch: string = '';

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
    this.selectedCommitsHasUpdated.emit();
  }

  updateBranchName(branch: string) {
    this.selectedBranch = branch;
    this.selectedBranchHasUpdated.emit();
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
    this.selectedCommitsHasUpdated.emit();
  }

  removeCommit(commit: Commit, device: string) {
    const pairFilterOutPredicate = (p: Pair) => !(p.commit.sha === commit.sha && p.device === device);
    this.selectedCommits.commitsAndDevices = this.selectedCommits.commitsAndDevices.filter(pairFilterOutPredicate);
    this.selectedCommitsHasUpdated.emit();
  }

  getSelectedCommits() {
    return this.selectedCommits;
  }

  getSelectedBranch() {
    return this.selectedBranch;
  }
}
