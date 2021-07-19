package com.parkview.parkview.database.exposed

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.benchmark.*
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.MissingBenchmarkResultException
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import javax.sql.DataSource

private object BenchmarkResultTable : Table() {
    override val tableName: String = "BenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40)
    override val primaryKey = PrimaryKey(id, name = "PK_MatrixBenchmarkResult_Id")
}

private object MatrixDatapointTable : Table() {
    override val tableName: String = "MatrixBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", BenchmarkResultTable.id)
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    val data: Column<String> = text("data")
    override val primaryKey = PrimaryKey(BenchmarkResultTable.id, name = "PK_MatrixDatapoint_Id")
}

private object BlasDatapointTable : Table() {
    override val tableName: String = "BlasBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", BenchmarkResultTable.id)
    val n: Column<Long> = long("n")
    val r: Column<Long> = long("r")
    val m: Column<Long> = long("m")
    val k: Column<Long> = long("k")
    val data: Column<String> = text("data")
    override val primaryKey = PrimaryKey(BenchmarkResultTable.id, name = "PK_BlasDatapoint_Id")
}

/**
 * This class handles database access using the Exposed library. It stores components like [Solver], [Preconditioner]
 * etc. as a json dump.
 *
 * @param source data source for database access
 */
class ExposedJsonHandler(source: DataSource) : DatabaseHandler {
    private var db: Database = Database.connect(datasource = source)
    private val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

    init {
        transaction(db) {
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(
                BenchmarkResultTable,
                MatrixDatapointTable,
                BlasDatapointTable,
            )
        }
    }

    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            val benchmarkId = transaction(db) {
                val query = BenchmarkResultTable.select {
                    (BenchmarkResultTable.device eq result.device.name) and
                            (BenchmarkResultTable.name eq result.benchmark.toString()) and
                            (BenchmarkResultTable.sha eq result.commit.sha)
                }

                if (query.count() > 0) {
                    query.first()[BenchmarkResultTable.id]
                } else {
                    BenchmarkResultTable.insert {
                        it[device] = result.device.name
                        it[sha] = result.commit.sha
                        it[name] = result.benchmark.toString()
                    }[BenchmarkResultTable.id]
                }
            }

            when (result.benchmark) {
                BenchmarkType.Spmv, BenchmarkType.Solver, BenchmarkType.Preconditioner, BenchmarkType.Conversion -> insertMatrixDatapoints(
                    result as MatrixBenchmarkResult,
                    benchmarkId
                )
                BenchmarkType.Blas -> insertBlasDatapoints(result as BlasBenchmarkResult, benchmarkId)
            }
        }
    }

    private fun insertMatrixDatapoints(result: MatrixBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            MatrixDatapointTable.batchInsert(result.datapoints) {
                this[MatrixDatapointTable.cols] = it.columns
                this[MatrixDatapointTable.rows] = it.rows
                this[MatrixDatapointTable.nonzeros] = it.nonzeros
                this[MatrixDatapointTable.data] = it.serializeComponentsToJson()
                this[MatrixDatapointTable.benchmarkId] = benchmarkId
            }
        }
    }

    private fun insertBlasDatapoints(result: BlasBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            BlasDatapointTable.batchInsert(result.datapoints) {
                this[BlasDatapointTable.n] = it.n
                this[BlasDatapointTable.m] = it.m
                this[BlasDatapointTable.k] = it.k
                this[BlasDatapointTable.r] = it.r
                this[BlasDatapointTable.data] = gson.toJson(it.operations)
                this[BlasDatapointTable.benchmarkId] = benchmarkId
            }
        }
    }

    // TODO: still about 200 lines too long, simplify this somehow
    override fun fetchBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val benchmarkId = transaction(db) {
            BenchmarkResultTable.select {
                (BenchmarkResultTable.device eq device.name) and
                        (BenchmarkResultTable.name eq benchmark.toString()) and
                        (BenchmarkResultTable.sha eq commit.sha)
            }.firstOrNull()?.get(BenchmarkResultTable.id)
        } ?: throw MissingBenchmarkResultException(commit, device, benchmark)



        return when (benchmark) {
            BenchmarkType.Spmv -> fetchSpmvBenchmarkResult(
                benchmarkId,
                commit,
                device,
                benchmark,
            )
            BenchmarkType.Solver -> fetchSolverBenchmarkResult(
                benchmarkId,
                commit,
                device,
                benchmark,
            )
            BenchmarkType.Preconditioner -> fetchPreconditionerBenchmarkResult(
                benchmarkId,
                commit,
                device,
                benchmark,
            )
            BenchmarkType.Conversion -> fetchConversionBenchmarkResult(
                benchmarkId,
                commit,
                device,
                benchmark,
            )
            BenchmarkType.Blas -> fetchBlasBenchmarkResult(benchmarkId, commit, device, benchmark)
        }
    }

    private fun fetchSpmvBenchmarkResult(
        benchmarkId: UUID,
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val arrayType = object : TypeToken<List<Format>>() {}.type
        val datapoints = transaction(db) {
            MatrixDatapointTable.select {
                MatrixDatapointTable.benchmarkId eq benchmarkId
            }.map {
                SpmvDatapoint(
                    rows = it[MatrixDatapointTable.rows],
                    columns = it[MatrixDatapointTable.cols],
                    nonzeros = it[MatrixDatapointTable.nonzeros],
                    formats = gson.fromJson(it[MatrixDatapointTable.data], arrayType),
                )
            }
        }

        return SpmvBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    private fun fetchConversionBenchmarkResult(
        benchmarkId: UUID,
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val arrayType = object : TypeToken<List<Conversion>>() {}.type
        val datapoints = transaction(db) {
            MatrixDatapointTable.select {
                MatrixDatapointTable.benchmarkId eq benchmarkId
            }.map {
                ConversionDatapoint(
                    rows = it[MatrixDatapointTable.rows],
                    columns = it[MatrixDatapointTable.cols],
                    nonzeros = it[MatrixDatapointTable.nonzeros],
                    conversions = gson.fromJson(it[MatrixDatapointTable.data], arrayType),
                )
            }
        }

        return ConversionBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    private fun fetchSolverBenchmarkResult(
        benchmarkId: UUID,
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val arrayType = object : TypeToken<List<Solver>>() {}.type
        val datapoints = transaction(db) {
            MatrixDatapointTable.select {
                MatrixDatapointTable.benchmarkId eq benchmarkId
            }.map {
                SolverDatapoint(
                    rows = it[MatrixDatapointTable.rows],
                    columns = it[MatrixDatapointTable.cols],
                    nonzeros = it[MatrixDatapointTable.nonzeros],
                    solvers = gson.fromJson(it[MatrixDatapointTable.data], arrayType),
                )
            }
        }

        return SolverBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    private fun fetchPreconditionerBenchmarkResult(
        benchmarkId: UUID,
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val arrayType = object : TypeToken<List<Preconditioner>>() {}.type
        val datapoints = transaction(db) {
            MatrixDatapointTable.select {
                MatrixDatapointTable.benchmarkId eq benchmarkId
            }.map {
                PreconditionerDatapoint(
                    rows = it[MatrixDatapointTable.rows],
                    columns = it[MatrixDatapointTable.cols],
                    nonzeros = it[MatrixDatapointTable.nonzeros],
                    preconditioners = gson.fromJson(it[MatrixDatapointTable.data], arrayType),
                )
            }
        }

        return PreconditionerBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    private fun fetchBlasBenchmarkResult(
        benchmarkId: UUID,
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
    ): BenchmarkResult {
        val arrayType = object : TypeToken<List<Operation>>() {}.type
        val datapoints = transaction(db) {
            BlasDatapointTable.select {
                (BlasDatapointTable.benchmarkId eq benchmarkId)
            }.map {
                BlasDatapoint(
                    n = it[BlasDatapointTable.n],
                    r = it[BlasDatapointTable.r],
                    k = it[BlasDatapointTable.k],
                    m = it[BlasDatapointTable.m],
                    operations = gson.fromJson(it[BlasDatapointTable.data], arrayType),
                )
            }
        }

        return BlasBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean = transaction(db) {
        BenchmarkResultTable.select {
            (BenchmarkResultTable.device eq device.name) and
                    (BenchmarkResultTable.name eq benchmark.toString()) and
                    (BenchmarkResultTable.sha eq commit.sha)
        }.count()
    } > 0

    override fun getAvailableDevices(commit: Commit, benchmark: BenchmarkType): List<Device> = transaction(db) {
        BenchmarkResultTable.select {
            (BenchmarkResultTable.name eq benchmark.toString()) and
                    (BenchmarkResultTable.sha eq commit.sha)
        }.map { it[BenchmarkResultTable.device] }.toSet().toList().map { Device(it) }
    }
}