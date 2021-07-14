package com.parkview.parkview.database

import BLAS_RESULT
import CONVERSION_RESULT
import SOLVER_RESULT
import SPMV_RESULT
import com.parkview.parkview.git.BenchmarkResult
import dirtyEquals
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sql.DataSource

internal class ExposedHandlerTest {
    private lateinit var dbHandler: DatabaseHandler

    @BeforeEach
    fun setup() {
        val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()
        val dataSource: DataSource = embeddedPostgres.postgresDatabase
        dbHandler = ExposedHandler(dataSource)
    }

    @Test
    fun `test storing and loading single spmv benchmark`() {
        val result = SPMV_RESULT

        dbHandler.insertBenchmarkResults(listOf(result))
        val returned: BenchmarkResult = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)

        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test storing and loading single conversion benchmark`() {
        val result = CONVERSION_RESULT

        dbHandler.insertBenchmarkResults(listOf(result))
        val returned: BenchmarkResult = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)

        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test storing and loading single blas benchmark`() {
        val result = BLAS_RESULT

        dbHandler.insertBenchmarkResults(listOf(result))
        val returned: BenchmarkResult = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)

        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test storing and loading solver benchmark`() {
        val result = SOLVER_RESULT

        dbHandler.insertBenchmarkResults(listOf(result))
        val returned: BenchmarkResult = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)

        println(result.toString())
        println(returned.toString())
        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test list available devices`() {
        dbHandler.insertBenchmarkResults(listOf(SPMV_RESULT))

        val devices = dbHandler.getAvailableDevices(SPMV_RESULT.commit, SPMV_RESULT.benchmark)
        assert(devices.first() == SPMV_RESULT.device)
    }

    @Test
    fun `test list available benchmarks`() {
        val results = listOf(SPMV_RESULT, BLAS_RESULT, CONVERSION_RESULT)

        dbHandler.insertBenchmarkResults(results)

        val benchmarks = dbHandler.getAvailableBenchmarks()

        results.forEach { assert(benchmarks.contains(it.benchmark)) }
    }

    @Test
    fun `test get benchmark format for benchmark name`() {
        val results = listOf(SPMV_RESULT, BLAS_RESULT, CONVERSION_RESULT)

        dbHandler.insertBenchmarkResults(results)

        results.forEach { assert(it.benchmark.type == dbHandler.getBenchmarkTypeForName(it.benchmark.name)) }

    }

    @Test
    fun `test hasDataAvailable with and without data`() {
        val result = BLAS_RESULT

        assert(!dbHandler.hasDataAvailable(result.commit, result.device, result.benchmark))

        dbHandler.insertBenchmarkResults(listOf(result))
        assert(dbHandler.hasDataAvailable(result.commit, result.device, result.benchmark))

    }
}