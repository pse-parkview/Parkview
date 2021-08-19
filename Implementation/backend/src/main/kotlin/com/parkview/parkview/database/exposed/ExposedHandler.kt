package com.parkview.parkview.database.exposed

import com.parkview.parkview.benchmark.*
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.MissingBenchmarkResultException
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource


/**
 * This class handles database access using the Exposed library. It stores components like [Solver], [Preconditioner]
 * etc. as a json dump.
 *
 * @param source data source for database access
 */
class ExposedHandler(source: DataSource) : DatabaseHandler {
    private var db: Database = Database.connect(datasource = source)

    init {
        transaction(db) {
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(
                BenchmarkResultTable,
                SpmvDatapointTable,
                PreconditionerDatapointTable,
                ConversionDatapointTable,
                SolverDatapointTable,
                BlasDatapointTable,
            )
        }
    }

    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            val benchmark = transaction(db) {
                val query = BenchmarkResultRow.find {
                    (BenchmarkResultTable.device eq result.device.name) and
                            (BenchmarkResultTable.name eq result.benchmark.toString()) and
                            (BenchmarkResultTable.sha eq result.commit.sha)
                }

                if (query.count() > 0) {
                    query.first()
                } else {
                    BenchmarkResultRow.new {
                        device = result.device.name
                        sha = result.commit.sha
                        name = result.benchmark.toString()
                    }
                }
            }

            when (result.benchmark) {
                BenchmarkType.Spmv -> insertSpmvBenchmarkResult(result as SpmvBenchmarkResult, benchmark)
                BenchmarkType.Conversion -> insertConversionBenchmarkResult(
                    result as ConversionBenchmarkResult,
                    benchmark
                )
                BenchmarkType.Solver -> insertSolverBenchmarkResult(result as SolverBenchmarkResult, benchmark)
                BenchmarkType.Preconditioner -> insertPreconditionerBenchmarkResult(
                    result as PreconditionerBenchmarkResult,
                    benchmark
                )
                BenchmarkType.Blas -> insertBlasBenchmarkResult(result as BlasBenchmarkResult, benchmark)
            }
        }
    }

    private fun insertSpmvBenchmarkResult(result: SpmvBenchmarkResult, benchmark: BenchmarkResultRow) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val query = SpmvDatapointRow.find {
                    (SpmvDatapointTable.benchmarkId eq benchmark.id) and
                            (SpmvDatapointTable.name eq datapoint.name) and
                            (SpmvDatapointTable.cols eq datapoint.columns) and
                            (SpmvDatapointTable.rows eq datapoint.rows) and
                            (SpmvDatapointTable.nonzeros eq datapoint.nonzeros)
                }

                if (query.count() > 0) {
                    val previousFormats: List<Format> = query.first().formats

                    val formats =
                        datapoint.formats + (previousFormats.filter { format -> datapoint.formats.find { it.name == format.name } == null })

                    query.first().formats = formats
                } else {
                    SpmvDatapointRow.new {
                        name = datapoint.name
                        cols = datapoint.columns
                        rows = datapoint.rows
                        nonzeros = datapoint.nonzeros
                        formats = datapoint.formats
                        this.benchmark = benchmark
                    }
                }
            }
        }
    }

    private fun insertConversionBenchmarkResult(result: ConversionBenchmarkResult, benchmark: BenchmarkResultRow) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val query = ConversionDatapointRow.find {
                    (ConversionDatapointTable.benchmarkId eq benchmark.id) and
                            (ConversionDatapointTable.name eq datapoint.name) and
                            (ConversionDatapointTable.cols eq datapoint.columns) and
                            (ConversionDatapointTable.rows eq datapoint.rows) and
                            (ConversionDatapointTable.nonzeros eq datapoint.nonzeros)
                }

                if (query.count() > 0) {
                    val previousConversions: List<Conversion> = query.first().conversions

                    val conversions =
                        datapoint.conversions + (previousConversions.filter { conversion -> datapoint.conversions.find { it.name == conversion.name } == null })

                    query.first().conversions = conversions
                } else {
                    ConversionDatapointRow.new {
                        name = datapoint.name
                        cols = datapoint.columns
                        rows = datapoint.rows
                        nonzeros = datapoint.nonzeros
                        conversions = datapoint.conversions
                        this.benchmark = benchmark
                    }
                }
            }
        }
    }

    private fun insertSolverBenchmarkResult(result: SolverBenchmarkResult, benchmark: BenchmarkResultRow) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val query = SolverDatapointRow.find {
                    (SolverDatapointTable.benchmarkId eq benchmark.id) and
                            (SolverDatapointTable.name eq datapoint.name) and
                            (SolverDatapointTable.cols eq datapoint.columns) and
                            (SolverDatapointTable.rows eq datapoint.rows) and
                            (SolverDatapointTable.nonzeros eq datapoint.nonzeros)
                }

                if (query.count() > 0) {
                    val previousSolvers: List<Solver> = query.first().solvers

                    val solvers =
                        datapoint.solvers + (previousSolvers.filter { solver -> datapoint.solvers.find { it.name == solver.name } == null })

                    query.first().solvers = solvers
                } else {
                    SolverDatapointRow.new {
                        name = datapoint.name
                        cols = datapoint.columns
                        rows = datapoint.rows
                        nonzeros = datapoint.nonzeros
                        solvers = datapoint.solvers
                        optimal = datapoint.optimal
                        this.benchmark = benchmark
                    }
                }
            }
        }
    }

    private fun insertPreconditionerBenchmarkResult(
        result: PreconditionerBenchmarkResult,
        benchmark: BenchmarkResultRow,
    ) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val query = PreconditionerDatapointRow.find {
                    (PreconditionerDatapointTable.benchmarkId eq benchmark.id) and
                            (PreconditionerDatapointTable.name eq datapoint.name) and
                            (PreconditionerDatapointTable.cols eq datapoint.columns) and
                            (PreconditionerDatapointTable.rows eq datapoint.rows) and
                            (PreconditionerDatapointTable.nonzeros eq datapoint.nonzeros)
                }

                if (query.count() > 0) {
                    val previousPreconditioners: List<Preconditioner> = query.first().preconditioners

                    val preconditioners =
                        datapoint.preconditioners + (previousPreconditioners.filter { preconditioner -> datapoint.preconditioners.find { it.name == preconditioner.name } == null })

                    query.first().preconditioners = preconditioners
                } else {
                    PreconditionerDatapointRow.new {
                        name = datapoint.name
                        cols = datapoint.columns
                        rows = datapoint.rows
                        nonzeros = datapoint.nonzeros
                        preconditioners = datapoint.preconditioners
                        this.benchmark = benchmark
                    }
                }
            }
        }
    }


    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult, benchmark: BenchmarkResultRow) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val query = BlasDatapointRow.find {
                    (BlasDatapointTable.benchmarkId eq benchmark.id) and
                            (BlasDatapointTable.n eq datapoint.n) and
                            (BlasDatapointTable.m eq datapoint.m) and
                            (BlasDatapointTable.k eq datapoint.k) and
                            (BlasDatapointTable.r eq datapoint.r)
                }

                if (query.count() > 0) {
                    val previousOperations: List<Operation> = query.first().operations

                    val operations =
                        datapoint.operations + previousOperations.filter { operation -> datapoint.operations.find { it.name == operation.name } == null }

                    query.first().operations = operations
                } else {
                    BlasDatapointRow.new {
                        n = datapoint.n
                        r = datapoint.r
                        k = datapoint.k
                        m = datapoint.m
                        operations = datapoint.operations
                        this.benchmark = benchmark
                    }
                }
            }
        }
    }

    // TODO: still about 200 lines too long, simplify this somehow
    override fun fetchBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val benchmarkQuery = transaction(db) {
            BenchmarkResultRow.find {
                (BenchmarkResultTable.device eq device.name) and
                        (BenchmarkResultTable.name eq benchmark.toString()) and
                        (BenchmarkResultTable.sha eq commit.sha)
            }.firstOrNull()
        } ?: throw MissingBenchmarkResultException(commit, device, benchmark)



        return when (benchmark) {
            BenchmarkType.Spmv -> fetchSpmvBenchmarkResult(
                benchmarkQuery,
                commit,
                device,
            )
            BenchmarkType.Solver -> fetchSolverBenchmarkResult(
                benchmarkQuery,
                commit,
                device,
            )
            BenchmarkType.Preconditioner -> fetchPreconditionerBenchmarkResult(
                benchmarkQuery,
                commit,
                device,
            )
            BenchmarkType.Conversion -> fetchConversionBenchmarkResult(
                benchmarkQuery,
                commit,
                device,
            )
            BenchmarkType.Blas -> fetchBlasBenchmarkResult(benchmarkQuery, commit, device)
        }
    }

    private fun fetchSpmvBenchmarkResult(
        benchmarkId: BenchmarkResultRow,
        commit: Commit,
        device: Device,
    ): BenchmarkResult {
        val datapoints = transaction(db) {
            SpmvDatapointRow.find {
                SpmvDatapointTable.benchmarkId eq benchmarkId.id
            }.map { it.toSpmvDatapoint() }
        }

        return SpmvBenchmarkResult(
            commit = commit,
            device = device,
            datapoints = datapoints,
        )
    }

    private fun fetchConversionBenchmarkResult(
        benchmarkId: BenchmarkResultRow,
        commit: Commit,
        device: Device,
    ): BenchmarkResult {
        val datapoints = transaction(db) {
            ConversionDatapointRow.find {
                ConversionDatapointTable.benchmarkId eq benchmarkId.id
            }.map { it.toConversionDatapoint() }
        }

        return ConversionBenchmarkResult(
            commit = commit,
            device = device,
            datapoints = datapoints,
        )
    }


    private fun fetchPreconditionerBenchmarkResult(
        benchmarkId: BenchmarkResultRow,
        commit: Commit,
        device: Device,
    ): BenchmarkResult {
        val datapoints = transaction(db) {
            PreconditionerDatapointRow.find {
                PreconditionerDatapointTable.benchmarkId eq benchmarkId.id
            }.map { it.toPreconditionerDatapoint() }
        }

        return PreconditionerBenchmarkResult(
            commit = commit,
            device = device,
            datapoints = datapoints,
        )
    }

    private fun fetchSolverBenchmarkResult(
        benchmarkId: BenchmarkResultRow,
        commit: Commit,
        device: Device,
    ): BenchmarkResult {
        val datapoints = transaction(db) {
            SolverDatapointRow.find {
                SolverDatapointTable.benchmarkId eq benchmarkId.id
            }.map { it.toSolverDatapoint() }
        }

        return SolverBenchmarkResult(
            commit = commit,
            device = device,
            datapoints = datapoints,
        )
    }

    private fun fetchBlasBenchmarkResult(
        benchmarkId: BenchmarkResultRow,
        commit: Commit,
        device: Device,
    ): BenchmarkResult {
        val datapoints = transaction(db) {
            BlasDatapointRow.find {
                BlasDatapointTable.benchmarkId eq benchmarkId.id
            }.map {
                it.toBlasDatapoint()
            }
        }

        return BlasBenchmarkResult(
            commit = commit,
            device = device,
            datapoints = datapoints,
        )
    }

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean = transaction(db) {
        BenchmarkResultRow.find {
            (BenchmarkResultTable.device eq device.name) and
                    (BenchmarkResultTable.name eq benchmark.toString()) and
                    (BenchmarkResultTable.sha eq commit.sha)
        }.count()
    } > 0

    override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
        transaction(db) {
            BenchmarkResultRow.find {
                (BenchmarkResultTable.name eq benchmark.toString()) and
                        (BenchmarkResultTable.sha eq commit.sha)
            }.map { it.device }.toSet().toList().map { Device(it) }
        }
}