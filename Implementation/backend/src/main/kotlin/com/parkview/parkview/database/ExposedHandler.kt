package com.parkview.parkview.database

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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

private object SpmvFormatTabe : Table() {
    override val tableName: String = "SpmvFormat"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40) // TODO either use TEXT or check about max format name length
    val completed: Column<Boolean> = bool("completed")
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(id, name = "PK_SpmvFormat_Id")
}

private object BlasBenchmarkResultTable : Table() {
    override val tableName: String = "BlasBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40) // TODO either use TEXT or check about max device name length
    val n: Column<Long> = long("n")
    val r: Column<Long> = long("r")
    val m: Column<Long> = long("m")
    val k: Column<Long> = long("k")
    override val primaryKey = PrimaryKey(BlasBenchmarkResultTable.id, name = "PK_BlasBenchmarkResult_Id")
}

private object BlasOperationTable : Table() {
    override val tableName: String = "BlasOperation"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40) // TODO either use TEXT or check about max format name length
    val time: Column<Double> = double("time")
    val flops: Column<Double> = double("flops")
    val bandwidth: Column<Double> = double("bandwidth")
    val repetitions: Column<Long> = long("repetitions")
    val completed: Column<Boolean> = bool("completed")
    override val primaryKey = PrimaryKey(BlasOperationTable.id, name = "PK_BlasOperation_Id")
}

/**
 * [DatabaseHandler] using the Exposed library
 */
class ExposedHandler : DatabaseHandler {
    // TODO: use spring stuff to get database object
//    val db = Database.connect(
//        "jdbc:postgresql://parkview-postgres:5432/parkview", driver = "org.postgresql.Driver",
//        user = "parkview", password = "parkview"
//    )
    val db = Database.connect(
        "jdbc:postgresql://localhost:5432/parkview", driver = "org.postgresql.Driver",
        user = "parkview", password = "parkview"
    )

    init {
        transaction {
            addLogger(StdOutSqlLogger) // debug logger
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(MatrixBenchmarkResultTable, SpmvFormatTabe)
        }
    }

    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            when (result) {
                is SpmvBenchmarkResult -> insertSmpvDatapoint(result)
                is BlasBenchmarkResult -> insertBlasBenchmarkResult(result)
            }
        }
    }

    private fun insertSmpvDatapoint(result: SpmvBenchmarkResult) {
        transaction {
            addLogger(StdOutSqlLogger) // debug logger

            // TODO use a batch insert instead of whatever this is
            for (datapoint in result.datapoints) {
                val benchmark = MatrixBenchmarkResultTable.insert {
                    it[sha] = result.commit.sha
                    it[name] = result.benchmark.name
                    it[device] = result.device.name
                    it[cols] = datapoint.columns
                    it[rows] = datapoint.rows
                    it[nonzeros] = datapoint.nonzeros
                }

                for (format in datapoint.formats) {
                    SpmvFormatTabe.insert {
                        it[benchmarkId] = benchmark[MatrixBenchmarkResultTable.id]
                        it[name] = format.name
                        it[completed] = format.completed
                        it[time] = format.time
                    }
                }
            }
            commit()
        }
    }

    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult) {
        transaction {
            addLogger(StdOutSqlLogger) // debug logger

            // TODO use a batch insert instead of whatever this is
            for (datapoint in result.datapoints) {
                val benchmark = BlasBenchmarkResultTable.insert {
                    it[sha] = result.commit.sha
                    it[name] = result.benchmark.name
                    it[device] = result.device.name
                    it[n] = datapoint.n
                    it[r] = datapoint.r
                    it[m] = datapoint.m
                    it[k] = datapoint.k
                }

                for (operation in datapoint.operations) {
                    BlasOperationTable.insert {
                        it[benchmarkId] = benchmark[BlasBenchmarkResultTable.id]
                        it[name] = operation.name
                        it[time] = operation.time
                        it[flops] = operation.flops
                        it[bandwidth] = operation.bandwidth
                        it[completed] = operation.completed
                        it[repetitions] = operation.repetitions
                    }
                }
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
        commitInfo: Commit,
        device: Device,
        benchmarkType: Benchmark,
        rowLim: Int,
        colLim: Int,
        nonzerosLim: Int
    ): BenchmarkResult {
        val datapoints = mutableListOf<SpmvDatapoint>()
        val benchmarks = transaction {
            MatrixBenchmarkResultTable.select {
                (MatrixBenchmarkResultTable.sha eq commitInfo.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmarkType.name) and
                        (MatrixBenchmarkResultTable.rows greaterEq rowLim) and
                        (MatrixBenchmarkResultTable.cols greaterEq colLim) and
                        (MatrixBenchmarkResultTable.nonzeros greaterEq nonzerosLim)
            }.toList()
        }

        // TODO: make this faster
        datapoints += benchmarks.map { spmvEntry ->
            SpmvDatapoint(
                rows = spmvEntry[MatrixBenchmarkResultTable.rows],
                columns = spmvEntry[MatrixBenchmarkResultTable.cols],
                nonzeros = spmvEntry[MatrixBenchmarkResultTable.nonzeros],
                formats = transaction {
                    SpmvFormatTabe
                        .select { SpmvFormatTabe.benchmarkId eq spmvEntry[MatrixBenchmarkResultTable.id] }
                        .map { formatEntry ->
                            Format(
                                name = formatEntry[SpmvFormatTabe.name],
                                time = formatEntry[SpmvFormatTabe.time],
                                completed = formatEntry[SpmvFormatTabe.completed],
                            )
                        }
                }
            )
        }
        println(datapoints)
        return SpmvBenchmarkResult(
            commit = commitInfo,
            device = device,
            benchmark = benchmarkType,
            datapoints = datapoints
        )
    }
}