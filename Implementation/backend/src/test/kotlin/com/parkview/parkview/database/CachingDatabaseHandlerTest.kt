package com.parkview.parkview.database

import COMMIT_A
import COMMIT_A_RESULT
import DEVICE
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CachingDatabaseHandlerTest {
    private val mockupHandler = object : DatabaseHandler {
        var dataAvailable = false

        override fun insertBenchmarkResults(results: List<BenchmarkResult>) {}

        override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult =
            COMMIT_A_RESULT.copy()

        override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean {
            dataAvailable = !dataAvailable
            return dataAvailable
        }

        override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
            listOf(DEVICE)
    }

    private lateinit var handlerWithMaxDuration: CachingDatabaseHandler
    private lateinit var handlerWithZeroDuration: CachingDatabaseHandler

    @BeforeTest
    fun setUp() {
        handlerWithMaxDuration = CachingDatabaseHandler(
            mockupHandler,
            maxCached = Int.MAX_VALUE
        )
        handlerWithZeroDuration = CachingDatabaseHandler(
            mockupHandler,
            maxCached = 0,
        )
    }

    @Test
    fun test_cleared_cache_after_insert() {
        val resultA = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        handlerWithMaxDuration.insertBenchmarkResults(listOf(COMMIT_A_RESULT))
        val resultB = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotSame(resultA, resultB)
    }

    @Test
    fun test_same_reference_for_fetched_benchmark() {
        val resultA = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val resultB = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertSame(resultA, resultB)
    }

    @Test
    fun test_same_value_for_data_available() {
        val a = handlerWithMaxDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val b = handlerWithMaxDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertEquals(a, b)
    }

    @Test
    fun test_same_reference_for_available_devices() {
        val devices1 = handlerWithMaxDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)
        val devices2 = handlerWithMaxDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)

        assertSame(devices1, devices2)
    }

    @Test
    fun test_timeout_for_fetched_benchmark() {
        val resultA = handlerWithZeroDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val resultB = handlerWithZeroDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotSame(resultA, resultB)
    }

    @Test
    fun test_timeout_for_data_available() {
        val a = handlerWithZeroDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val b = handlerWithZeroDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotEquals(a, b)
    }

    @Test
    fun test_timeout_for_available_devices() {
        val devices1 = handlerWithZeroDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)
        val devices2 = handlerWithZeroDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)

        assertNotSame(devices1, devices2)
    }
}
