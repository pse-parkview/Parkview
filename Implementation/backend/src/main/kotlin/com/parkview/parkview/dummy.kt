package com.parkview.parkview

import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.MissingBenchmarkResultException
import com.parkview.parkview.git.*

val DUMMY_BRANCH = "test"
val DUMMY_DEVICE = Device("meter")
val DUMMY_COMMIT = Commit("peter")

val SPMV_DUMMY_RESULT = SpmvBenchmarkResult(DUMMY_COMMIT, DUMMY_DEVICE, (1..5).map {
    SpmvDatapoint(
        "Datapoint_$it", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
        listOf(
            Format(
                name = "format_name", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true
            )
        ),
    )
}
)

class DummyRepositoryHandler : RepositoryHandler {
    init {
        DUMMY_COMMIT.addDevice(DUMMY_DEVICE)
    }

    override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        if (branch == DUMMY_BRANCH) {
            listOf(DUMMY_COMMIT)
        } else {
            emptyList()
        }

    override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        if (rev == DUMMY_COMMIT.sha) {
            listOf(DUMMY_COMMIT)
        } else {
            emptyList()
        }

    override fun getAvailableBranches(): List<String> = listOf(DUMMY_BRANCH)

    override fun getNumberOfPages(branch: String): Int = 1
}

class DummyDatabaseHandler : DatabaseHandler {
    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        return
    }

    override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult =
        if (hasDataAvailable(commit, device, benchmark)) {
            SPMV_DUMMY_RESULT
        } else {
            throw MissingBenchmarkResultException(commit, device, benchmark)
        }

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean =
        commit == DUMMY_COMMIT && device == DUMMY_DEVICE && benchmark == BenchmarkType.Spmv


    override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
        if (commit == DUMMY_COMMIT && benchmark == BenchmarkType.Spmv) {
            listOf(DUMMY_DEVICE)
        } else {
            emptyList()
        }
}