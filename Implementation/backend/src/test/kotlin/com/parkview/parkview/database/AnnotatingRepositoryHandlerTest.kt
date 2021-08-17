package com.parkview.parkview.database

import COMMIT_A
import COMMIT_B
import com.parkview.parkview.git.*
import org.junit.jupiter.api.Test

internal class AnnotatingRepositoryHandlerTest {
    private object MockDatabaseHandler : DatabaseHandler {
        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
            throw IllegalAccessException("mockup code that should not be used")
        }

        override fun fetchBenchmarkResult(
            commit: Commit,
            device: Device,
            benchmark: BenchmarkType,
        ): BenchmarkResult {
            throw IllegalAccessException("mockup code that should not be used")
        }

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType) = commit.sha == COMMIT_A.sha

        override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
            if ((commit.sha == COMMIT_A.sha) and (benchmark == BenchmarkType.Blas)) listOf(Device("gamer")) else emptyList()
    }

    private object MockRepositoryHandler : RepositoryHandler {
        private val commits = listOf(COMMIT_A, COMMIT_B)

        override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> = commits

        override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
            TODO("Not yet implemented")
        }

        override fun getAvailableBranches(): List<String> {
            throw IllegalAccessException("mockup code that should not be used")
        }

        override fun getNumberOfPages(branch: String): Int {
            throw IllegalAccessException("mockup code that should not be used")
        }

    }

    private val annotatingRepositoryHandler = AnnotatingRepositoryHandler(MockRepositoryHandler, MockDatabaseHandler)


    @Test
    fun `tests annotation of commits with database results`() {
        val commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assert(commits.first().availableDevices == listOf(Device("gamer")))
        assert(commits[1].availableDevices.isEmpty())
    }

    @Test
    fun `tests annotation of commits with database results doesn't apply twice`() {
        var commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assert(commits.first().availableDevices == listOf(Device("gamer")))
        assert(commits[1].availableDevices.isEmpty())
        commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assert(commits.first().availableDevices == listOf(Device("gamer")))
        assert(commits[1].availableDevices.isEmpty())
    }

    @Test
    fun `test annotation of commits with benchmark type change`() {
        var commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assert(commits.first().availableDevices == listOf(Device("gamer")))
        assert(commits[1].availableDevices.isEmpty())
        commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Conversion)

        assert(commits.first().availableDevices.isEmpty())
        assert(commits[1].availableDevices.isEmpty())
    }
}