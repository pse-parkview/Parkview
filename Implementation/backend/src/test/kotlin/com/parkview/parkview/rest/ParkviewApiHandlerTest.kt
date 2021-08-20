package com.parkview.parkview.rest

import COMMIT_A_RESULT
import com.parkview.parkview.benchmark.BenchmarkJsonParser
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.tracking.PerformanceTracker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ParkviewApiHandlerTest {
    private lateinit var apiHandler: ParkviewApiHandler

    private val repHandler = object : RepositoryHandler {
        override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
            return listOf(COMMIT_A_RESULT.commit.apply { addDevice(COMMIT_A_RESULT.device) })
        }

        override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
            emptyList()

        override fun getAvailableBranches(): List<String> = listOf("test")

        override fun getNumberOfPages(branch: String): Int = 1
    }

    private val databaseHandler = object : DatabaseHandler {
        val postedResults = mutableListOf<BenchmarkResult>()

        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
            postedResults += results
        }

        override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult =
            if (commit.sha == COMMIT_A_RESULT.commit.sha) COMMIT_A_RESULT else throw IllegalStateException("Shouldn't happen in this test")

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean {
            TODO("Not yet implemented")
        }

        override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> {
            TODO("Not yet implemented")
        }
    }

    private val jsonParser = object : BenchmarkJsonParser {
        override fun benchmarkResultsFromJson(sha: String, deviceName: String, json: String): List<BenchmarkResult> {
            if (sha == COMMIT_A_RESULT.commit.sha) return listOf(COMMIT_A_RESULT)
            return emptyList()
        }
    }

    @BeforeEach
    fun setup() {
        databaseHandler.postedResults.clear()
        apiHandler = ParkviewApiHandler(
            repHandler,
            databaseHandler,
            PerformanceTracker(databaseHandler, repHandler),
            jsonParser,
        )
    }

    @Test
    fun `post a single benchmark result`() {
        apiHandler.postBenchmarkResults(COMMIT_A_RESULT.commit.sha, COMMIT_A_RESULT.device.name, "[]")
        assertTrue(databaseHandler.postedResults.contains(COMMIT_A_RESULT))
    }

    @Test
    fun `retrieve devices for branch`() {
        val devices = apiHandler.getAvailableDevices("test", COMMIT_A_RESULT.benchmark)
        println(devices)
        assertNotNull(devices.find { it == COMMIT_A_RESULT.device })
    }

    @Test
    fun `retrieve history`() {
        val history = apiHandler.getHistory("test", 1, COMMIT_A_RESULT.benchmark.toString())
        assertNotNull(history.find { it.sha == COMMIT_A_RESULT.commit.sha })
    }

    @Test
    fun getAvailableBranches() {
        val branches = apiHandler.getAvailableBranches()
        assertTrue(branches.contains("test"))
    }

    @Test
    fun getAvailableBenchmarks() {
        val benchmarks = apiHandler.getAvailableBenchmarks()
        assertEquals(BenchmarkType.values().map { it.toString() }, benchmarks)
    }

    @Test
    fun getNumberOfPages() {
        val numberOfPages = apiHandler.getNumberOfPages("test")
        assertEquals(numberOfPages, 1)
    }
}
