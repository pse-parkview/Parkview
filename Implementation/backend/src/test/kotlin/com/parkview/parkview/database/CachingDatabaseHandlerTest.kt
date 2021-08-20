package com.parkview.parkview.database

import COMMIT_A
import COMMIT_A_RESULT
import DEVICE
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

    @BeforeEach
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
    fun `test cleared cache after insert`() {
        val resultA = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        handlerWithMaxDuration.insertBenchmarkResults(listOf(COMMIT_A_RESULT))
        val resultB = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotSame(resultA, resultB)
    }

    @Test
    fun `test same reference for fetched benchmark`() {
        val resultA = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val resultB = handlerWithMaxDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertSame(resultA, resultB)
    }

    @Test
    fun `test same value for data available`() {
        val a = handlerWithMaxDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val b = handlerWithMaxDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertEquals(a, b)
    }

    @Test
    fun `test same reference for available devices`() {
        val devices1 = handlerWithMaxDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)
        val devices2 = handlerWithMaxDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)

        assertSame(devices1, devices2)
    }

    @Test
    fun `test timeout for fetched benchmark`() {
        val resultA = handlerWithZeroDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val resultB = handlerWithZeroDuration.fetchBenchmarkResult(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotSame(resultA, resultB)
    }

    @Test
    fun `test timeout for data available`() {
        val a = handlerWithZeroDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)
        val b = handlerWithZeroDuration.hasDataAvailable(COMMIT_A, DEVICE, COMMIT_A_RESULT.benchmark)

        assertNotEquals(a, b)
    }

    @Test
    fun `test timeout for available devices`() {
        val devices1 = handlerWithZeroDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)
        val devices2 = handlerWithZeroDuration.getAvailableDevicesForCommit(COMMIT_A, COMMIT_A_RESULT.benchmark)

        assertNotSame(devices1, devices2)
    }
}
