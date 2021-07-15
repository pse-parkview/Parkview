package com.parkview.parkview.database.exposed

import BLAS_RESULT
import CONVERSION_RESULT
import SOLVER_RESULT
import SPMV_RESULT
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import dirtyEquals
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sql.DataSource

internal class ExposedJsonHandlerTest {
    private lateinit var dbHandler: DatabaseHandler

    @BeforeEach
    fun setup() {
        val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()
        val dataSource: DataSource = embeddedPostgres.postgresDatabase
        dbHandler = ExposedJsonHandler(dataSource)
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


        println(result.toString())
        println(returned.toString())
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
        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test list available devices`() {
        dbHandler.insertBenchmarkResults(listOf(SPMV_RESULT))

        val devices = dbHandler.getAvailableDevices(SPMV_RESULT.commit, SPMV_RESULT.benchmark)
        assert(devices.first() == SPMV_RESULT.device)
    }

    @Test
    fun `test hasDataAvailable with and without data`() {
        val result = BLAS_RESULT

        assert(!dbHandler.hasDataAvailable(result.commit, result.device, result.benchmark))

        dbHandler.insertBenchmarkResults(listOf(result))
        assert(dbHandler.hasDataAvailable(result.commit, result.device, result.benchmark))

    }

    @Test
    fun `aggregation of datapoints for spmv`() {
        val result = SPMV_RESULT

        dbHandler.insertBenchmarkResults(listOf(result))
        var returned = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)
        assert((returned as SpmvBenchmarkResult).datapoints.size == 5)
        dbHandler.insertBenchmarkResults(listOf(result))
        returned = dbHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)
        assert((returned as SpmvBenchmarkResult).datapoints.size == 10)
    }

}

