package com.parkview.parkview.database

import com.parkview.parkview.benchmark.*
import com.parkview.parkview.git.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager
import java.util.*

private object MatrixBenchmarkResultTable : Table() {
    override val tableName: String = "MatrixBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40) // TODO either use TEXT or check about max device name length
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    override val primaryKey = PrimaryKey(id, name = "PK_MatrixBenchmarkResult_Id")
}

private object SpmvFormatTable : Table() {
    override val tableName: String = "SpmvFormat"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val completed: Column<Boolean> = bool("completed")
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(id, name = "PK_SpmvFormat_Id")
}

private object BlasBenchmarkResultTable : Table() {
    override val tableName: String = "BlasBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40)
    val n: Column<Long> = long("n")
    val r: Column<Long> = long("r")
    val m: Column<Long> = long("m")
    val k: Column<Long> = long("k")
    override val primaryKey = PrimaryKey(id, name = "PK_BlasBenchmarkResult_Id")
}

private object BlasOperationTable : Table() {
    override val tableName: String = "BlasOperation"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", BlasBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val time: Column<Double> = double("time")
    val flops: Column<Double> = double("flops")
    val bandwidth: Column<Double> = double("bandwidth")
    val repetitions: Column<Long> = long("repetitions")
    val completed: Column<Boolean> = bool("completed")
    override val primaryKey = PrimaryKey(id, name = "PK_BlasOperation_Id")
}

/**
 * [DatabaseHandler] using the Exposed library
 */
class ExposedHandler : DatabaseHandler {
    // TODO: use spring stuff to get database object
    // TODO: enable batch insert for database controller
//    val url = "jdbc:postgresql://parkview-postgres:5432/parkview"
    private val url = "jdbc:postgresql://localhost:5432/parkview"
    private val props = Properties()
        .apply { setProperty("user", "parkview") }
        .apply { setProperty("password", "parkview") }
        .apply { setProperty("reWriteBatchedInserts", "true") }

    private val db = Database.connect(getNewConnection = { DriverManager.getConnection(url, props) })


    init {
        transaction(db) {
            addLogger(StdOutSqlLogger) // debug logger
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(
                MatrixBenchmarkResultTable,
                SpmvFormatTable,
                BlasBenchmarkResultTable,
                BlasOperationTable
            )
        }
    }

    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            when (result) {
                is SpmvBenchmarkResult -> insertSpmvDatapoint(result)
                is BlasBenchmarkResult -> insertBlasBenchmarkResult(result)
            }
        }
    }

    private fun insertSpmvDatapoint(result: SpmvBenchmarkResult) {
        val listOfFormats: List<List<Format>> = result.datapoints.map { it.formats }
        val allBenchmarkIDs = transaction(db) {
            addLogger(StdOutSqlLogger) // debug logger
            MatrixBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[MatrixBenchmarkResultTable.sha] = result.commit.sha
                this[MatrixBenchmarkResultTable.name] = result.benchmark.name
                this[MatrixBenchmarkResultTable.device] = result.device.name
                this[MatrixBenchmarkResultTable.cols] = datapoint.columns
                this[MatrixBenchmarkResultTable.rows] = datapoint.rows
                this[MatrixBenchmarkResultTable.nonzeros] = datapoint.nonzeros
            }
        }

        val formatsWithId = allBenchmarkIDs.zip(listOfFormats).fold(emptyList<Pair<UUID, Format>>()) { acc, pair ->
            acc + pair.second.map {
                Pair(
                    pair.first[MatrixBenchmarkResultTable.id],
                    it
                )
            }.toList()
        }

        transaction(db) {
            addLogger(StdOutSqlLogger) // debug logger
            SpmvFormatTable.batchInsert(formatsWithId) { (id, format) ->
                this[SpmvFormatTable.benchmarkId] = id
                this[SpmvFormatTable.name] = format.name
                this[SpmvFormatTable.completed] = format.completed
                this[SpmvFormatTable.time] = format.time
            }

            commit()
        }
    }

    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult) {
        val listOfOperations: List<List<Operation>> = result.datapoints.map { it.operations }
        val allBenchmarkIDs = transaction(db) {
            addLogger(StdOutSqlLogger) // debug logger
            BlasBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[BlasBenchmarkResultTable.sha] = result.commit.sha
                this[BlasBenchmarkResultTable.name] = result.benchmark.name
                this[BlasBenchmarkResultTable.device] = result.device.name
                this[BlasBenchmarkResultTable.n] = datapoint.n
                this[BlasBenchmarkResultTable.r] = datapoint.r
                this[BlasBenchmarkResultTable.m] = datapoint.m
                this[BlasBenchmarkResultTable.k] = datapoint.k
            }
        }
        val operationsWithId =
            allBenchmarkIDs.zip(listOfOperations).fold(emptyList<Pair<UUID, Operation>>()) { acc, pair ->
                acc + pair.second.map {
                    Pair(
                        pair.first[MatrixBenchmarkResultTable.id],
                        it
                    )
                }.toList()
            }
        transaction(db) {
            addLogger(StdOutSqlLogger) // debug logger
            BlasOperationTable.batchInsert(operationsWithId) { (id, operation) ->
                this[BlasOperationTable.benchmarkId] = id
                this[BlasOperationTable.name] = operation.name
                this[BlasOperationTable.completed] = operation.completed
                this[BlasOperationTable.time] = operation.time
                this[BlasOperationTable.flops] = operation.flops
                this[BlasOperationTable.bandwidth] = operation.bandwidth
                this[BlasOperationTable.repetitions] = operation.repetitions
            }

            commit()
        }
    }

    override fun fetchBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: Benchmark,
        rowLim: Int,
        colLim: Int,
        nonzerosLim: Int
    ) = when (benchmark.type) {
        BenchmarkType.SpmvBenchmark -> fetchSpmvResult(commit, device, benchmark, rowLim, colLim, nonzerosLim)
        BenchmarkType.SolverBenchmark -> TODO()
        BenchmarkType.PreconditionerBenchmark -> TODO()
        BenchmarkType.ConversionBenchmark -> TODO()
        BenchmarkType.BlasBenchmark -> TODO()
    }

    private fun fetchSpmvResult(
        commit: Commit,
        device: Device,
        benchmarkType: Benchmark,
        rowLim: Int,
        colLim: Int,
        nonzerosLim: Int
    ): BenchmarkResult {
        val datapoints = mutableListOf<SpmvDatapoint>()
        val benchmarks = transaction(db) {
            MatrixBenchmarkResultTable.select {
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmarkType.name) and
                        (MatrixBenchmarkResultTable.rows greaterEq rowLim) and
                        (MatrixBenchmarkResultTable.cols greaterEq colLim) and
                        (MatrixBenchmarkResultTable.nonzeros greaterEq nonzerosLim)
            }.toList()
        }

        datapoints += benchmarks.map { spmvEntry ->
            SpmvDatapoint(
                rows = spmvEntry[MatrixBenchmarkResultTable.rows],
                columns = spmvEntry[MatrixBenchmarkResultTable.cols],
                nonzeros = spmvEntry[MatrixBenchmarkResultTable.nonzeros],
                formats = transaction(db) {
                    SpmvFormatTable
                        .select { SpmvFormatTable.benchmarkId eq spmvEntry[MatrixBenchmarkResultTable.id] }
                        .map { formatEntry ->
                            Format(
                                name = formatEntry[SpmvFormatTable.name],
                                time = formatEntry[SpmvFormatTable.time],
                                completed = formatEntry[SpmvFormatTable.completed],
                            )
                        }
                }
            )
        }
        return SpmvBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmarkType,
            datapoints = datapoints
        )
    }
}