package com.parkview.parkview.database

import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

private object MatrixBenchmarkResult : Table() {
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40) // TODO either use TEXT or check about max device name length
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    override val primaryKey = PrimaryKey(id, name = "PK_MatrixBenchmarkResult_Id")
}

private object SpmvFormat : Table() {
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResult.id)
    val name: Column<String> = varchar("name", 40) // TODO either use TEXT or check about max format name length
    val completed: Column<Boolean> = bool("completed")
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(id, name = "PK_SpmvFormat_Id")
}


/**
 * [DatabaseHandler] for accessing a PostgreSQL database.
 */
class ExposedHandler : DatabaseHandler {
    // TODO: use spring stuff to get database object
    val db = Database.connect(
        "jdbc:postgresql://parkview-postgres:5432/parkview", driver = "org.postgresql.Driver",
        user = "parkview", password = "parkview"
    )

    init {
        transaction {
            addLogger(StdOutSqlLogger) // debug logger
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(MatrixBenchmarkResult, SpmvFormat)
        }

    }

    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        transaction {
            addLogger(StdOutSqlLogger) // debug logger

            // TODO use a batch insert instead of whatever this is
            for (result in results) {
                if (result !is SpmvBenchmarkResult) continue
                for (datapoint in result.datapoints) {
                    val benchmark = MatrixBenchmarkResult.insert {
                        it[sha] = result.commit.sha
                        it[name] = result.benchmark.name
                        it[device] = result.device.name
                        it[cols] = datapoint.columns
                        it[rows] = datapoint.rows
                        it[nonzeros] = datapoint.nonzeros
                    }

                    for (format in datapoint.formats) {
                        SpmvFormat.insert {
                            it[benchmarkId] = benchmark[MatrixBenchmarkResult.id]
                            it[name] = format.name
                            it[completed] = format.completed
                            it[time] = format.time
                        }
                    }
                }
            }

            commit()
        }
    }

    override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: Benchmark) = when (benchmark.type) {
        BenchmarkType.SpmvBenchmark -> fetchSpmvResult(commit, device, benchmark)
        BenchmarkType.SolverBenchmark -> TODO()
        BenchmarkType.PreconditionerBenchmark -> TODO()
        BenchmarkType.ConversionBenchmark -> TODO()
        BenchmarkType.BlasBenchmark -> TODO()
    }

    private fun fetchSpmvResult(commitInfo: Commit, device: Device, benchmarkType: Benchmark): BenchmarkResult {
        val datapoints = mutableListOf<SpmvDatapoint>()
        transaction {
            val benchmarks = MatrixBenchmarkResult.select {
                (MatrixBenchmarkResult.sha eq commitInfo.sha) and
                        (MatrixBenchmarkResult.device eq device.name) and
                        (MatrixBenchmarkResult.name eq benchmarkType.name) and
                        (MatrixBenchmarkResult.rows eq 1138)
            }

            // TODO: make this faster
            datapoints += benchmarks.map { spmvEntry ->
                SpmvDatapoint(
                    rows = spmvEntry[MatrixBenchmarkResult.rows],
                    columns = spmvEntry[MatrixBenchmarkResult.cols],
                    nonzeros = spmvEntry[MatrixBenchmarkResult.nonzeros],
                    formats = SpmvFormat
                        .select { SpmvFormat.benchmarkId eq spmvEntry[MatrixBenchmarkResult.id] }
                        .map { formatEntry ->
                            Format(
                                name = formatEntry[SpmvFormat.name],
                                time = formatEntry[SpmvFormat.time],
                                completed = formatEntry[SpmvFormat.completed],
                            )
                        }.toList()
                )
            }
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