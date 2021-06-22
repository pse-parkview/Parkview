package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit

class MissingCommitException(commit: Commit): Exception(
    "Error, the commit with the sha ${commit.sha} could not be found."
)

class MissingBenchmarkResultException(benchmarkResult: BenchmarkResult): Exception(
    "Error, the benchmark result for ${benchmarkResult.benchmark.name} on " +
            "commit ${benchmarkResult.commit.sha} using device ${benchmarkResult.device.name} could not be found."
)

class MissingBranchException(branchName: String): Exception(
    "Error, the branch with name $branchName could not be found."
)
