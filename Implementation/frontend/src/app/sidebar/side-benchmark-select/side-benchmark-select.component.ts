import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-side-benchmark-select',
  templateUrl: './side-benchmark-select.component.html',
  styleUrls: ['./side-benchmark-select.component.scss']
})
export class SideBenchmarkSelectComponent implements OnInit {

  currentlySelectedBenchmark: Benchmark = "Solver";

  constructor() {
  }

  ngOnInit(): void {
  }

  benchmarks: Benchmark[] = ["Blas", "Conversion", "Preconditioner", "Solver", "SpMV"];


  selectBenchmark(benchmark: Benchmark) {
    this.currentlySelectedBenchmark = benchmark;
  }
}

type Benchmark = "Blas" | "Conversion" | "Preconditioner" | "Solver" | "SpMV"
