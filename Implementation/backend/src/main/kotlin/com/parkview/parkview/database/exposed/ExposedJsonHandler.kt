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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    override val tableName: String = "MatrixDatapointResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", BenchmarkResultTable.id)
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    val data: Column<String> = text("data")
    override val primaryKey = PrimaryKey(BenchmarkResultTable.id, name = "PK_MatrixDatapoint_Id")
}

private object BlasDatapointTable : Table() {
    override val tableName: String = "BlasDatapoint"
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
                BenchmarkType.Spmv -> insertSpmvBenchmarkResult(result as SpmvBenchmarkResult, benchmarkId)
                BenchmarkType.Conversion -> insertConversionBenchmarkResult(
                    result as ConversionBenchmarkResult,
                    benchmarkId
                )
                BenchmarkType.Solver -> insertSolverBenchmarkResult(result as SolverBenchmarkResult, benchmarkId)
                BenchmarkType.Preconditioner -> insertPreconditionerBenchmarkResult(
                    result as PreconditionerBenchmarkResult,
                    benchmarkId
                )
                BenchmarkType.Blas -> insertBlasBenchmarkResult(result as BlasBenchmarkResult, benchmarkId)
            }
        }
    }

    private fun insertSpmvBenchmarkResult(result: SpmvBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val selectionQuery = (
                        (MatrixDatapointTable.benchmarkId eq benchmarkId) and
                                (MatrixDatapointTable.cols eq datapoint.columns) and
                                (MatrixDatapointTable.rows eq datapoint.rows) and
                                (MatrixDatapointTable.nonzeros eq datapoint.nonzeros)
                        )
                val query = MatrixDatapointTable.select {
                    selectionQuery
                }

                if (query.count() > 0) {
                    val type = object : TypeToken<List<Format>>() {}.type
                    val previousFormats: List<Format> = gson.fromJson(query.first()[MatrixDatapointTable.data], type)

                    val formats =
                        datapoint.formats + (previousFormats.filter { format -> datapoint.formats.find { it.name == format.name } == null })

                    MatrixDatapointTable.update({
                        selectionQuery
                    }) {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(formats)
                        it[this.benchmarkId] = benchmarkId
                    }
                } else {
                    MatrixDatapointTable.insert {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(datapoint.formats)
                        it[this.benchmarkId] = benchmarkId
                    }
                }
            }
        }
    }

    private fun insertConversionBenchmarkResult(result: ConversionBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val selectionQuery = ((MatrixDatapointTable.benchmarkId eq benchmarkId) and
                        (MatrixDatapointTable.cols eq datapoint.columns) and
                        (MatrixDatapointTable.rows eq datapoint.rows) and
                        (MatrixDatapointTable.nonzeros eq datapoint.nonzeros))
                val query = MatrixDatapointTable.select {
                    selectionQuery
                }

                if (query.count() > 0) {
                    val type = object : TypeToken<List<Conversion>>() {}.type
                    val previousFormats: List<Format> = gson.fromJson(query.first()[MatrixDatapointTable.data], type)

                    val conversions =
                        datapoint.conversions + (previousFormats.filter { conversion -> datapoint.conversions.find { it.name == conversion.name } == null })

                    MatrixDatapointTable.update({ selectionQuery }) {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(conversions)
                        it[this.benchmarkId] = benchmarkId
                    }
                } else {
                    MatrixDatapointTable.insert {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(datapoint.conversions)
                        it[this.benchmarkId] = benchmarkId
                    }
                }
            }
        }
    }

    private fun insertSolverBenchmarkResult(result: SolverBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val selectionQuery = ((MatrixDatapointTable.benchmarkId eq benchmarkId) and
                        (MatrixDatapointTable.cols eq datapoint.columns) and
                        (MatrixDatapointTable.rows eq datapoint.rows) and
                        (MatrixDatapointTable.nonzeros eq datapoint.nonzeros))
                val query = MatrixDatapointTable.select {
                    selectionQuery
                }

                if (query.count() > 0) {
                    val type = object : TypeToken<List<Solver>>() {}.type
                    val previousFormats: List<Format> = gson.fromJson(query.first()[MatrixDatapointTable.data], type)

                    val solvers =
                        datapoint.solvers + (previousFormats.filter { solver -> datapoint.solvers.find { it.name == solver.name } == null })

                    MatrixDatapointTable.update({ selectionQuery }) {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(solvers)
                        it[this.benchmarkId] = benchmarkId
                    }
                } else {
                    MatrixDatapointTable.insert {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(datapoint.solvers)
                        it[this.benchmarkId] = benchmarkId
                    }
                }
            }
        }
    }

    private fun insertPreconditionerBenchmarkResult(result: PreconditionerBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val selectionQuery = ((MatrixDatapointTable.benchmarkId eq benchmarkId) and
                        (MatrixDatapointTable.cols eq datapoint.columns) and
                        (MatrixDatapointTable.rows eq datapoint.rows) and
                        (MatrixDatapointTable.nonzeros eq datapoint.nonzeros))
                val query = MatrixDatapointTable.select {
                    selectionQuery
                }

                if (query.count() > 0) {
                    val type = object : TypeToken<List<Preconditioner>>() {}.type
                    val previousFormats: List<Format> = gson.fromJson(query.first()[MatrixDatapointTable.data], type)

                    val preconditioners =
                        datapoint.preconditioners + (previousFormats.filter { preconditioner -> datapoint.preconditioners.find { it.name == preconditioner.name } == null })

                    MatrixDatapointTable.update({ selectionQuery }) {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(preconditioners)
                        it[this.benchmarkId] = benchmarkId
                    }
                } else {
                    MatrixDatapointTable.insert {
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                        it[data] = gson.toJson(datapoint.preconditioners)
                        it[this.benchmarkId] = benchmarkId
                    }
                }
            }
        }
    }

    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult, benchmarkId: UUID) {
        transaction(db) {
            for (datapoint in result.datapoints) {
                val selectionQuery = ((BlasDatapointTable.benchmarkId eq benchmarkId) and
                        (BlasDatapointTable.n eq datapoint.n) and
                        (BlasDatapointTable.m eq datapoint.m) and
                        (BlasDatapointTable.k eq datapoint.k) and
                        (BlasDatapointTable.r eq datapoint.r))
                val query = BlasDatapointTable.select {
                    selectionQuery
                }

                if (query.count() > 0) {
                    val type = object : TypeToken<List<Operation>>() {}.type
                    val previousOperations: List<Operation> =
                        gson.fromJson(query.first()[BlasDatapointTable.data], type)

                    println(datapoint.operations)
                    val operations =
                        datapoint.operations + previousOperations.filter { operation -> datapoint.operations.find { it.name == operation.name } == null }
                    println(operations)

                    BlasDatapointTable.update({ selectionQuery }) {
                        it[n] = datapoint.n
                        it[r] = datapoint.r
                        it[k] = datapoint.k
                        it[m] = datapoint.m
                        it[data] = gson.toJson(operations)
                        it[this.benchmarkId] = benchmarkId
                    }
                } else {
                    BlasDatapointTable.insert {
                        it[n] = datapoint.n
                        it[r] = datapoint.r
                        it[k] = datapoint.k
                        it[m] = datapoint.m
                        it[data] = gson.toJson(datapoint.operations)
                        it[this.benchmarkId] = benchmarkId
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