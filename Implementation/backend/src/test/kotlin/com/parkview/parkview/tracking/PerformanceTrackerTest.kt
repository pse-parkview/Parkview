package com.parkview.parkview.tracking

import COMMIT_A
import COMMIT_A_RESULT
import COMMIT_B
import COMMIT_B_RESULT
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.parkview.parkview.git.RepositoryHandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class PerformanceTrackerTest {
    private val newBenchmarkResult = COMMIT_A_RESULT
    private val oldBenchmarkResult = COMMIT_B_RESULT

    private val webhook = object : Webhook {
        var new: BenchmarkResult? = null
        var old: BenchmarkResult? = null
        override fun addResult(new: BenchmarkResult, previous: BenchmarkResult) {
            this.new = new
            this.old = previous
        }

        override fun notifyHook() {}
    }

    private val repositoryHandler = object : RepositoryHandler {
        private val commits = listOf(
            Commit("0".repeat(40)),
            COMMIT_A,
            Commit("1".repeat(40)),
            Commit("2".repeat(40)),
            Commit("3".repeat(40)),
            Commit("4".repeat(40)),
            Commit("5".repeat(40)),
            Commit("6".repeat(40)),
            COMMIT_B,
            Commit("7".repeat(40)),
            Commit("8".repeat(40)),
            Commit("9".repeat(40)),
        )

        override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
            TODO("Not yet implemented")
        }

        override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
            if (page > 1) return emptyList()
            val history = commits.toMutableList()

            for (commit in commits) {
                if (commit.sha == rev) return history

                history.remove(commit)
            }

            return history
        }

        override fun getAvailableBranches(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getNumberOfPages(branch: String): Int {
            TODO("Not yet implemented")
        }
    }

    private val databaseHandler = object : DatabaseHandler {
        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {}

        override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult =
            when (commit.sha) {
                COMMIT_A.sha -> newBenchmarkResult
                COMMIT_B.sha -> oldBenchmarkResult
                else -> fail("This commits does not exist in the database")
            }

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean =
            ((commit == COMMIT_A) || (commit == COMMIT_B))

        override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> {
            TODO("Not yet implemented")
        }
    }

    lateinit var tracker: PerformanceTracker

    @BeforeEach
    fun setup() {
        webhook.new = null
        webhook.old = null
        tracker = PerformanceTracker(
            databaseHandler,
            repositoryHandler,
        )

        tracker.addWebhook(webhook)
    }

    @Test
    fun `retrieving the correct commits`() {
        tracker.notifyHooks(listOf(newBenchmarkResult))

        assertNotNull(webhook.new)
        assertNotNull(webhook.old)
        assertEquals(webhook.new, newBenchmarkResult)
        assertEquals(webhook.old, oldBenchmarkResult)
    }

    @Test
    fun `no previous commit`() {
        tracker.notifyHooks(listOf(oldBenchmarkResult))

        assertNull(webhook.new)
        assertNull(webhook.old)
    }
}
