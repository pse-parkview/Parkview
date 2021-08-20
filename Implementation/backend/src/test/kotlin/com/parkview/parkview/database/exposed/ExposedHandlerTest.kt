package com.parkview.parkview.database.exposed

import BLAS_RESULT
import COMMIT_A
import CONVERSION_RESULT
import DEVICE
import PRECONDITIONER_RESULT
import SOLVER_RESULT
import SPMV_RESULT
import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.MissingBenchmarkResultException
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import dirtyEquals
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

        println(result.toString())
        println(returned.toString())
        assert(result.dirtyEquals(returned))
    }

    @Test
    fun `test storing and loading single preconditioner benchmark`() {
        val result = PRECONDITIONER_RESULT

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

        val devices = dbHandler.getAvailableDevicesForCommit(SPMV_RESULT.commit, SPMV_RESULT.benchmark)
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
    fun `test joins component runs after each other BLAS`() {
        val resultA = BlasBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                BlasDatapoint(
                    it.toLong() * 10,
                    operations = listOf(
                        Operation("A", 1.0, 1.0, it * 1.0, true),
                    )
                )
            }
        )
        val resultB = BlasBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                BlasDatapoint(
                    it.toLong() * 10,
                    operations = listOf(
                        Operation("B", 1.0, 1.0, it * 1.0, true),
                    )
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA))
        var returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Blas) as BlasBenchmarkResult
        for (datapoint in returned.datapoints) {
            assert(datapoint.operations.size == 1)
        }
        dbHandler.insertBenchmarkResults(listOf(resultB))

        returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Blas) as BlasBenchmarkResult
        println(returned.toString())
        for (datapoint in returned.datapoints) {
            assert(datapoint.operations.size == 2)
        }
    }

    @Test
    fun `test joins component runs after each other SPMV`() {
        val resultA = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "B", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        val resultB = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA))
        var returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Spmv) as SpmvBenchmarkResult
        for (datapoint in returned.datapoints) {
            assert(datapoint.formats.size == 1)
        }
        dbHandler.insertBenchmarkResults(listOf(resultB))

        returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Spmv) as SpmvBenchmarkResult
        println(returned.toString())
        for (datapoint in returned.datapoints) {
            assert(datapoint.formats.size == 2)
        }
    }

    @Test
    fun `test joins component runs same insert SPMV`() {
        val resultA = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "B", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        val resultB = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA, resultB))
        val returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Spmv) as SpmvBenchmarkResult
        for (datapoint in returned.datapoints) {
            assert(datapoint.formats.size == 2)
        }
    }

    @Test
    fun `test replaces component runs same insert SPMV`() {
        val resultA = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        val resultB = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                    listOf(
                        format
                    ),
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA, resultB))
        val returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Spmv) as SpmvBenchmarkResult
        for (datapoint in returned.datapoints) {
            assert(datapoint.formats.size == 1)
        }
    }

    @Test
    fun `test joins component runs same insert BLAS`() {
        val resultA = BlasBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                BlasDatapoint(
                    (it * 10).toLong(),
                    operations = listOf(
                        Operation("A", 1.0, 1.0, it * 1.0, true),
                    )
                )
            }
        )
        val resultB = BlasBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                BlasDatapoint(
                    (it * 10).toLong(),
                    operations = listOf(
                        Operation("B", 1.0, 1.0, it * 1.0, true),
                    )
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA, resultB))
        val returned =
            dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, BenchmarkType.Blas) as BlasBenchmarkResult
        for (datapoint in returned.datapoints) {
            assert(datapoint.operations.size == 2)
        }
    }

    @Test
    fun `aggregation of datapoints for spmv`() {
        val resultA = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 2 + 1, it.toLong() * 2 + 1, it.toLong() * 2 + 1,
                    listOf(
                        format
                    ),
                )
            }
        )

        val resultB = SpmvBenchmarkResult(
            COMMIT_A,
            DEVICE,
            (1..5).map {
                val format = Format(name = "A", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
                SpmvDatapoint(
                    "", it.toLong() * 2, it.toLong() * 2, it.toLong() * 2,
                    listOf(
                        format
                    ),
                )
            }
        )

        dbHandler.insertBenchmarkResults(listOf(resultA))
        var returned = dbHandler.fetchBenchmarkResult(resultA.commit, resultA.device, resultA.benchmark)
        assert((returned as SpmvBenchmarkResult).datapoints.size == 5)
        dbHandler.insertBenchmarkResults(listOf(resultB))
        returned = dbHandler.fetchBenchmarkResult(resultB.commit, resultB.device, resultB.benchmark)
        assert((returned as SpmvBenchmarkResult).datapoints.size == 10)
    }

    @Test
    fun `test fetching benchmark result that doesn't exist`() {
        assertThrows<MissingBenchmarkResultException> {
            dbHandler.fetchBenchmarkResult(COMMIT_A, DEVICE, BenchmarkType.Blas)
        }
    }
}
