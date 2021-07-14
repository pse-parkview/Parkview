package com.parkview.parkview.database

import com.parkview.parkview.benchmark.*
import com.parkview.parkview.git.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import javax.sql.DataSource

private object BenchmarkTypeTable : Table() {
    override val tableName: String = "BenchmarkType"
    val name: Column<String> = varchar("name", 30).uniqueIndex()
    val format: Column<String> = varchar("format", 30)
    override val primaryKey = PrimaryKey(BenchmarkTypeTable.name, name = "PK_BenchmarkType_Name")
}

private object MatrixBenchmarkResultTable : Table() {
    override val tableName: String = "MatrixBenchmarkResult"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40)
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    override val primaryKey = PrimaryKey(id, name = "PK_MatrixBenchmarkResult_Id")
}

private object ConversionTable : Table() {
    override val tableName = "Conversion"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val completed: Column<Boolean> = bool("completed")
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(ConversionTable.id, name = "PK_Conversion_Id")
}

private object SpmvFormatTable : Table() {
    override val tableName: String = "SpmvFormat"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val completed: Column<Boolean> = bool("completed")
    val storage: Column<Long> = long("storage")
    val maxRelativeNorm2: Column<Double> = double("max_relative_norm2")
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

private object ConnectionPoolSource {
    private val config: HikariConfig = HikariConfig()
    val ds: HikariDataSource

    init {
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/parkview"
        config.username = "parkview"
        config.password = "parkview"
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.addDataSourceProperty("reWriteBatchedInserts", "true")
        ds = HikariDataSource(config)
    }
}

/**
 * [DatabaseHandler] using the Exposed library
 */
class ExposedHandler(source: DataSource) : DatabaseHandler {
    // TODO: use spring stuff to get database object
    // TODO: enable batch insert for database controller
    private var db: Database = Database.connect(datasource = source)

    companion object {
        fun withConnectionPool() = ExposedHandler(ConnectionPoolSource.ds)
    }

    init {
        transaction(db) {
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(
                MatrixBenchmarkResultTable,
                ConversionTable,
                SpmvFormatTable,
                BlasBenchmarkResultTable,
                BlasOperationTable,
                BenchmarkTypeTable,
            )
        }
    }

    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            insertOrUpdateTypeTable(result)
            when (result.benchmark.type) {
                BenchmarkType.Spmv -> insertSpmvBenchmarkResult(result as SpmvBenchmarkResult)
                BenchmarkType.Solver -> TODO("SOLVER NOT YET IMPLEMENTED")
                BenchmarkType.Preconditioner -> TODO("PRECONDITIONER NOT YET IMPLEMENTED")
                BenchmarkType.Conversion -> insertConversionBenchmarkResult(result as ConversionBenchmarkResult)
                BenchmarkType.Blas -> insertBlasBenchmarkResult(result as BlasBenchmarkResult)
            }
        }
    }

    private fun insertOrUpdateTypeTable(result: BenchmarkResult) {
        transaction {
            if (BenchmarkTypeTable.select { BenchmarkTypeTable.name eq result.benchmark.name }.count() > 0) {
                BenchmarkTypeTable.update({ BenchmarkTypeTable.name eq result.benchmark.name }) {
                    it[format] = result.benchmark.type.name
                }
            } else {
                BenchmarkTypeTable.insert {
                    it[name] = result.benchmark.name
                    it[format] = result.benchmark.type.name
                }
            }
        }
    }

    private fun insertConversionBenchmarkResult(result: ConversionBenchmarkResult) {
        val listOfConversions: List<List<Conversion>> = result.datapoints.map { it.conversions }
        val allBenchmarkIDs = transaction(db) {
            MatrixBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[MatrixBenchmarkResultTable.sha] = result.commit.sha
                this[MatrixBenchmarkResultTable.name] = result.benchmark.name
                this[MatrixBenchmarkResultTable.device] = result.device.name
                this[MatrixBenchmarkResultTable.cols] = datapoint.columns
                this[MatrixBenchmarkResultTable.rows] = datapoint.rows
                this[MatrixBenchmarkResultTable.nonzeros] = datapoint.nonzeros
            }
        }

        val conversionsWithId =
            allBenchmarkIDs.zip(listOfConversions).fold(emptyList<Pair<UUID, Conversion>>()) { acc, pair ->
                acc + pair.second.map {
                    Pair(
                        pair.first[MatrixBenchmarkResultTable.id],
                        it
                    )
                }.toList()
            }

        transaction(db) {
            ConversionTable.batchInsert(conversionsWithId) { (id, conversion) ->
                this[ConversionTable.benchmarkId] = id
                this[ConversionTable.name] = conversion.name
                this[ConversionTable.completed] = conversion.completed
                this[ConversionTable.time] = conversion.time
            }

            commit()
        }
    }

    private fun insertSpmvBenchmarkResult(result: SpmvBenchmarkResult) {
        val listOfFormats: List<List<Format>> = result.datapoints.map { it.formats }
        val allBenchmarkIDs = transaction(db) {
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
            SpmvFormatTable.batchInsert(formatsWithId) { (id, format) ->
                this[SpmvFormatTable.benchmarkId] = id
                this[SpmvFormatTable.name] = format.name
                this[SpmvFormatTable.completed] = format.completed
                this[SpmvFormatTable.time] = format.time
                this[SpmvFormatTable.storage] = format.storage
                this[SpmvFormatTable.maxRelativeNorm2] = format.maxRelativeNorm2
            }

            commit()
        }
    }

    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult) {
        val listOfOperations: List<List<Operation>> = result.datapoints.map { it.operations }
        val allBenchmarkIDs = transaction(db) {
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
                        pair.first[BlasBenchmarkResultTable.id],
                        it
                    )
                }.toList()
            }
        transaction(db) {
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
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ) = when (benchmark.type) {
        BenchmarkType.Spmv -> fetchSpmvResult(commit, device, benchmark, rowLim, colLim, nonzerosLim)
        BenchmarkType.Solver -> TODO()
        BenchmarkType.Preconditioner -> TODO()
        BenchmarkType.Conversion -> fetchConversionBenchmarkResult(
            commit,
            device,
            benchmark,
            rowLim,
            colLim,
            nonzerosLim
        )
        BenchmarkType.Blas -> fetchBlasBenchmarkResult(commit, device, benchmark)
    }

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: Benchmark) = when (benchmark.type) {
        BenchmarkType.Blas -> transaction(db) {
            BlasBenchmarkResultTable.select(
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.device eq device.name) and
                        (BlasBenchmarkResultTable.name eq benchmark.name)
            ).count() > 0
        }
        else -> transaction(db) {
            MatrixBenchmarkResultTable.select(
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmark.name)
            ).count() > 0
        }
    }

    override fun getAvailableDevices(commit: Commit, benchmark: Benchmark): List<Device> = when (benchmark.type) {
        BenchmarkType.Blas -> transaction(db) {
            BlasBenchmarkResultTable.slice(BlasBenchmarkResultTable.device, BlasBenchmarkResultTable.name).select {
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.name eq benchmark.name)
            }.map { it[BlasBenchmarkResultTable.device] }.toSet().map { Device(name = it) }.toList()
        }
        else -> transaction(db) {
            MatrixBenchmarkResultTable.slice(MatrixBenchmarkResultTable.device, MatrixBenchmarkResultTable.name)
                .select {
                    (MatrixBenchmarkResultTable.sha eq commit.sha) and
                            (MatrixBenchmarkResultTable.name eq benchmark.name)
                }.map { it[MatrixBenchmarkResultTable.device] }.toSet().map { Device(name = it) }.toList()
        }
    }

    override fun getAvailableBenchmarks(): List<Benchmark> = transaction(db) {
        BenchmarkTypeTable.selectAll()
            .map { Benchmark(it[BenchmarkTypeTable.name], BenchmarkType.valueOf(it[BenchmarkTypeTable.format])) }
    }

    private fun fetchSpmvResult(
        commit: Commit,
        device: Device,
        benchmarkType: Benchmark,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
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
                                storage = formatEntry[SpmvFormatTable.storage],
                                maxRelativeNorm2 = formatEntry[SpmvFormatTable.maxRelativeNorm2],
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

    private fun fetchConversionBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmarkType: Benchmark,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ): BenchmarkResult {
        val datapoints = mutableListOf<ConversionDatapoint>()
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

        datapoints += benchmarks.map { conversionEntry ->
            ConversionDatapoint(
                rows = conversionEntry[MatrixBenchmarkResultTable.rows],
                columns = conversionEntry[MatrixBenchmarkResultTable.cols],
                nonzeros = conversionEntry[MatrixBenchmarkResultTable.nonzeros],
                conversions = transaction(db) {
                    ConversionTable
                        .select { ConversionTable.benchmarkId eq conversionEntry[MatrixBenchmarkResultTable.id] }
                        .map { conversionEntry ->
                            Conversion(
                                name = conversionEntry[ConversionTable.name],
                                time = conversionEntry[ConversionTable.time],
                                completed = conversionEntry[ConversionTable.completed],
                            )
                        }
                }
            )
        }
        return ConversionBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmarkType,
            datapoints = datapoints
        )
    }

    private fun fetchBlasBenchmarkResult(commit: Commit, device: Device, benchmark: Benchmark): BenchmarkResult {
        val datapoints = mutableListOf<BlasDatapoint>()
        val benchmarks = transaction(db) {
            BlasBenchmarkResultTable.select {
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.device eq device.name) and
                        (BlasBenchmarkResultTable.name eq benchmark.name)
            }.toList()
        }

        datapoints += benchmarks.map { blasEntry ->
            BlasDatapoint(
                n = blasEntry[BlasBenchmarkResultTable.n],
                r = blasEntry[BlasBenchmarkResultTable.r],
                m = blasEntry[BlasBenchmarkResultTable.m],
                k = blasEntry[BlasBenchmarkResultTable.k],
                operations = transaction(db) {
                    BlasOperationTable
                        .select { BlasOperationTable.benchmarkId eq blasEntry[BlasBenchmarkResultTable.id] }
                        .map { operationEntry ->
                            Operation(
                                name = operationEntry[BlasOperationTable.name],
                                time = operationEntry[BlasOperationTable.time],
                                completed = operationEntry[BlasOperationTable.completed],
                                bandwidth = operationEntry[BlasOperationTable.bandwidth],
                                flops = operationEntry[BlasOperationTable.flops],
                                repetitions = operationEntry[BlasOperationTable.repetitions],
                            )
                        }
                }
            )
        }
        return BlasBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }

    override fun getBenchmarkTypeForName(benchmarkName: String): BenchmarkType {
        val firstOccurrence = transaction(db) {
            BenchmarkTypeTable.select { BenchmarkTypeTable.name eq benchmarkName }
                .firstOrNull()?.get(BenchmarkTypeTable.format)
                ?: throw Exception("This benchmark name does not exist in the database")
        }

        return BenchmarkType.valueOf(firstOccurrence)
    }
}