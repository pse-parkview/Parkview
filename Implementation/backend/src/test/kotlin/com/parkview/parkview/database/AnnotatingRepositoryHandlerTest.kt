package com.parkview.parkview.database

import COMMIT_A
import COMMIT_B
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.parkview.parkview.git.RepositoryHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AnnotatingRepositoryHandlerTest {
    private object MockDatabaseHandler : DatabaseHandler {
        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
            throw UnsupportedOperationException("mockup code that should not be used")
        }

        override fun fetchBenchmarkResult(
            commit: Commit,
            device: Device,
            benchmark: BenchmarkType,
        ): BenchmarkResult {
            throw UnsupportedOperationException("mockup code that should not be used")
        }

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType) =
            commit.sha == COMMIT_A.sha

        override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
            if ((commit.sha == COMMIT_A.sha) and (benchmark == BenchmarkType.Blas)) listOf(Device("gamer")) else emptyList()
    }

    private object MockRepositoryHandler : RepositoryHandler {
        private val commits = listOf(COMMIT_A, COMMIT_B)

        override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
            commits

        override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
            TODO("Not yet implemented")
        }

        override fun getAvailableBranches(): List<String> {
            throw UnsupportedOperationException("mockup code that should not be used")
        }

        override fun getNumberOfPages(branch: String): Int {
            throw UnsupportedOperationException("mockup code that should not be used")
        }
    }

    private val annotatingRepositoryHandler = AnnotatingRepositoryHandler(MockRepositoryHandler, MockDatabaseHandler)

    @Test
    fun tests_annotation_of_commits_with_database_results() {
        val commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assertEquals(commits.first().availableDevices, listOf(Device("gamer")))
        assertTrue(commits[1].availableDevices.isEmpty())
    }

    @Test
    fun tests_annotation_of_commits_with_database_results_doesnt_apply_twice() {
        var commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assertEquals(commits.first().availableDevices, listOf(Device("gamer")))
        assertTrue(commits[1].availableDevices.isEmpty())
        commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assertEquals(commits.first().availableDevices, listOf(Device("gamer")))
        assertTrue(commits[1].availableDevices.isEmpty())
    }

    @Test
    fun test_annotation_of_commits_with_benchmark_type_change() {
        var commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Blas)

        assertEquals(commits.first().availableDevices, listOf(Device("gamer")))
        assertTrue(commits[1].availableDevices.isEmpty())
        commits = annotatingRepositoryHandler.fetchGitHistoryByBranch("", 1, BenchmarkType.Conversion)

        assertTrue(commits.first().availableDevices.isEmpty())
        assertTrue(commits[1].availableDevices.isEmpty())
    }
}
