package com.parkview.parkview.database

import COMMIT_A
import COMMIT_B
import com.parkview.parkview.git.*
import org.junit.jupiter.api.Test

internal class AnnotatingRepositoryHandlerTest {
    private object MockDatabaseHandler : DatabaseHandler {
        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
            TODO("Not yet implemented")
        }

        override fun fetchBenchmarkResult(
            commit: Commit,
            device: Device,
            benchmark: BenchmarkType,
            rowLim: Long,
            colLim: Long,
            nonzerosLim: Long
        ): BenchmarkResult {
            TODO("Not yet implemented")
        }

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType) = commit.sha == "sha"

        override fun getAvailableDevices(commit: Commit, benchmark: BenchmarkType): List<Device> =
            if (commit.sha == "sha") listOf(Device("gamer")) else emptyList()
    }

    private object MockRepositoryHandler : RepositoryHandler {
        private val commits = listOf(COMMIT_A, COMMIT_B)
        override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> = commits

        override fun getAvailableBranches(): List<String> {
            TODO("Not yet implemented")
        }

    }

    private val annotatingRepositoryHandler = AnnotatingRepositoryHandler(MockRepositoryHandler, MockDatabaseHandler)


    @Test
    fun `tests annotation of commits with database results`() {
        val commits = annotatingRepositoryHandler.fetchGitHistory("", 1, BenchmarkType.Blas)

        assert(commits.first().devices == listOf(Device("gamer")))
        assert(commits[1].devices.isEmpty())
    }

    @Test
    fun `tests annotation of commits with database results doesn't apply twice`() {
        var commits = annotatingRepositoryHandler.fetchGitHistory("", 1, BenchmarkType.Blas)

        assert(commits.first().devices == listOf(Device("gamer")))
        assert(commits[1].devices.isEmpty())
        commits = annotatingRepositoryHandler.fetchGitHistory("", 1, BenchmarkType.Blas)

        assert(commits.first().devices == listOf(Device("gamer")))
        assert(commits[1].devices.isEmpty())

    }
}