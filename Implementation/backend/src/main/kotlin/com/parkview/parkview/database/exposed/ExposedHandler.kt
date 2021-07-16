package com.parkview.parkview.database.exposed

import com.parkview.parkview.benchmark.*
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import javax.sql.DataSource

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

private object SolverTable : Table() {
    override val tableName: String = "Solver"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val rhsNorm: Column<Double> = double("rhs_norm").default(0.0)
    val residualNorm: Column<Double> = double("residual_norm").default(0.0)
    val completed: Column<Boolean> = bool("completed")
    val generateTime: Column<Double> = double("generate_time")
    val applyTime: Column<Double> = double("apply_time")
    val applyIterations: Column<Long> = long("apply_iterations")
    override val primaryKey = PrimaryKey(SolverTable.id, name = "PK_Solver_Id")
}

private object SolverGenerateComponentTable : Table() {
    override val tableName: String = "SolverGenerateComponent"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val name: Column<String> = varchar("name", 40)
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_SolverGenerateComponent_Id")
}

private object SolverApplyComponentTable : Table() {
    override val tableName: String = "SolverApplyComponent"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val name: Column<String> = varchar("name", 40)
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_SolverApplyComponent_Id")
}

private object RecurrentResidualsTable : Table() {
    override val tableName: String = "RecurrentResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_RecurrentResiduals_Id")
}

private object TrueResidualsTable : Table() {
    override val tableName: String = "TrueResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_TrueResiduals_Id")
}

private object ImplicitResidualsTable : Table() {
    override val tableName: String = "ImplicitResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_ImplicitResiduals_Id")
}

private object IterationTimestampsTable : Table() {
    override val tableName: String = "IterationTimestamps"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_IterationTimestamps_Id")
}

/**
 * [DatabaseHandler] using the Exposed library
 */
@Deprecated("Terrible implementation, kept in case ExposedJsonHandler has it's own problems.")
class ExposedHandler(source: DataSource) : DatabaseHandler {
    // TODO: use spring stuff to get database object
    // TODO: enable batch insert for database controller
    private var db: Database = Database.connect(datasource = source)

    constructor(jdbcUrl: String, username: String, password: String) : this(
        HikariDataSource(
            HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
            }
        )
    )

    init {
        transaction(db) {
            SchemaUtils.createSchema(Schema("parkview"))
            SchemaUtils.create(
                MatrixBenchmarkResultTable,
                ConversionTable,
                SpmvFormatTable,
                BlasBenchmarkResultTable,
                BlasOperationTable,
                SolverTable,
                RecurrentResidualsTable,
                ImplicitResidualsTable,
                TrueResidualsTable,
                IterationTimestampsTable,
                SolverApplyComponentTable,
                SolverGenerateComponentTable,
            )
        }
    }

    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        for (result in results) {
            when (result.benchmark) {
                BenchmarkType.Spmv -> insertSpmvBenchmarkResult(result as SpmvBenchmarkResult)
                BenchmarkType.Solver -> insertSolverBenchmarkResult(result as SolverBenchmarkResult)
                BenchmarkType.Preconditioner -> TODO("PRECONDITIONER NOT YET IMPLEMENTED")
                BenchmarkType.Conversion -> insertConversionBenchmarkResult(result as ConversionBenchmarkResult)
                BenchmarkType.Blas -> insertBlasBenchmarkResult(result as BlasBenchmarkResult)
            }
        }
    }

    private fun insertConversionBenchmarkResult(result: ConversionBenchmarkResult) {
        val listOfConversions: List<List<Conversion>> = result.datapoints.map { it.conversions }
        val allBenchmarkIDs = transaction(db) {
            MatrixBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[MatrixBenchmarkResultTable.sha] = result.commit.sha
                this[MatrixBenchmarkResultTable.name] = result.benchmark.toString()
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
                this[MatrixBenchmarkResultTable.name] = result.benchmark.toString()
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

    private fun insertSolverBenchmarkResult(result: SolverBenchmarkResult) {
        val listOfSolvers = result.datapoints.map { it.solvers }
        val allBenchmarkIDs = transaction(db) {
            MatrixBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[MatrixBenchmarkResultTable.sha] = result.commit.sha
                this[MatrixBenchmarkResultTable.name] = result.benchmark.toString()
                this[MatrixBenchmarkResultTable.device] = result.device.name
                this[MatrixBenchmarkResultTable.cols] = datapoint.columns
                this[MatrixBenchmarkResultTable.rows] = datapoint.rows
                this[MatrixBenchmarkResultTable.nonzeros] = datapoint.nonzeros
            }
        }

        val solverWithId = allBenchmarkIDs.zip(listOfSolvers).fold(emptyList<Pair<UUID, Solver>>()) { acc, pair ->
            acc + pair.second.map {
                Pair(
                    pair.first[MatrixBenchmarkResultTable.id],
                    it
                )
            }.toList()
        }

        val allSolverIds = transaction(db) {
            SolverTable.batchInsert(solverWithId) { (id, solver) ->
                this[SolverTable.benchmarkId] = id
                this[SolverTable.name] = solver.name
                this[SolverTable.completed] = solver.completed
                this[SolverTable.rhsNorm] = solver.rhsNorm
                this[SolverTable.generateTime] = solver.generateTotalTime
                this[SolverTable.applyTime] = solver.applyTotalTime
                this[SolverTable.applyIterations] = solver.applyIterations
                this[SolverTable.residualNorm] = solver.residualNorm
            }
        }

        transaction(db) {
            for ((id, solverWithBenchmarkId) in allSolverIds.zip(solverWithId)) {
                val solver = solverWithBenchmarkId.second
                RecurrentResidualsTable.batchInsert(solver.recurrentResiduals) {
                    this[RecurrentResidualsTable.solverId] = id[SolverTable.id]
                    this[RecurrentResidualsTable.value] = it
                }
                TrueResidualsTable.batchInsert(solver.trueResiduals) {
                    this[TrueResidualsTable.solverId] = id[SolverTable.id]
                    this[TrueResidualsTable.value] = it
                }
                ImplicitResidualsTable.batchInsert(solver.implicitResiduals) {
                    this[ImplicitResidualsTable.solverId] = id[SolverTable.id]
                    this[ImplicitResidualsTable.value] = it
                }
                IterationTimestampsTable.batchInsert(solver.iterationTimestamps) {
                    this[IterationTimestampsTable.solverId] = id[SolverTable.id]
                    this[IterationTimestampsTable.value] = it
                }
                SolverApplyComponentTable.batchInsert(solver.generateComponents) {
                    this[SolverApplyComponentTable.solverId] = id[SolverTable.id]
                    this[SolverApplyComponentTable.name] = it.name
                    this[SolverApplyComponentTable.time] = it.runtime
                }
                SolverGenerateComponentTable.batchInsert(solver.generateComponents) {
                    this[SolverGenerateComponentTable.solverId] = id[SolverTable.id]
                    this[SolverGenerateComponentTable.name] = it.name
                    this[SolverGenerateComponentTable.time] = it.runtime
                }
            }
            commit()
        }
    }

    private fun insertBlasBenchmarkResult(result: BlasBenchmarkResult) {
        val listOfOperations: List<List<Operation>> = result.datapoints.map { it.operations }
        val allBenchmarkIDs = transaction(db) {
            BlasBenchmarkResultTable.batchInsert(result.datapoints) { datapoint ->
                this[BlasBenchmarkResultTable.sha] = result.commit.sha
                this[BlasBenchmarkResultTable.name] = result.benchmark.toString()
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


    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType) = when (benchmark) {
        BenchmarkType.Blas -> transaction(db) {
            BlasBenchmarkResultTable.select(
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.device eq device.name) and
                        (BlasBenchmarkResultTable.name eq benchmark.toString())
            ).count() > 0
        }
        else -> transaction(db) {
            MatrixBenchmarkResultTable.select(
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmark.toString())
            ).count() > 0
        }
    }

    override fun getAvailableDevices(commit: Commit, benchmark: BenchmarkType): List<Device> = when (benchmark) {
        BenchmarkType.Blas -> transaction(db) {
            BlasBenchmarkResultTable.slice(BlasBenchmarkResultTable.device, BlasBenchmarkResultTable.name).select {
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.name eq benchmark.toString())
            }.map { it[BlasBenchmarkResultTable.device] }.toSet().map { Device(name = it) }.toList()
        }
        else -> transaction(db) {
            MatrixBenchmarkResultTable.slice(MatrixBenchmarkResultTable.device, MatrixBenchmarkResultTable.name)
                .select {
                    (MatrixBenchmarkResultTable.sha eq commit.sha) and
                            (MatrixBenchmarkResultTable.name eq benchmark.toString())
                }.map { it[MatrixBenchmarkResultTable.device] }.toSet().map { Device(name = it) }.toList()
        }
    }

    override fun fetchBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ) = when (benchmark) {
        BenchmarkType.Spmv -> fetchSpmvResult(commit, device, benchmark, rowLim, colLim, nonzerosLim)
        BenchmarkType.Solver -> fetchSolverResult(commit, device, benchmark, rowLim, colLim, nonzerosLim)
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

    private fun fetchSpmvResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ): BenchmarkResult {
        val datapoints = mutableListOf<SpmvDatapoint>()
        val benchmarks = transaction(db) {
            MatrixBenchmarkResultTable.select {
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmark.toString()) and
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
            benchmark = benchmark,
            datapoints = datapoints
        )
    }

    private fun fetchConversionBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ): BenchmarkResult {
        val datapoints = mutableListOf<ConversionDatapoint>()
        val benchmarks = transaction(db) {
            MatrixBenchmarkResultTable.select {
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmark.toString()) and
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
            benchmark = benchmark,
            datapoints = datapoints
        )
    }

    private fun fetchBlasBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult {
        val datapoints = mutableListOf<BlasDatapoint>()
        val benchmarks = transaction(db) {
            BlasBenchmarkResultTable.select {
                (BlasBenchmarkResultTable.sha eq commit.sha) and
                        (BlasBenchmarkResultTable.device eq device.name) and
                        (BlasBenchmarkResultTable.name eq benchmark.toString())
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

    private fun fetchSolverResult(
        commit: Commit,
        device: Device,
        benchmark: BenchmarkType,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long,
    ): BenchmarkResult {
        val datapoints = mutableListOf<SolverDatapoint>()
        val benchmarks = transaction(db) {
            MatrixBenchmarkResultTable.select {
                (MatrixBenchmarkResultTable.sha eq commit.sha) and
                        (MatrixBenchmarkResultTable.device eq device.name) and
                        (MatrixBenchmarkResultTable.name eq benchmark.toString()) and
                        (MatrixBenchmarkResultTable.rows greaterEq rowLim) and
                        (MatrixBenchmarkResultTable.cols greaterEq colLim) and
                        (MatrixBenchmarkResultTable.nonzeros greaterEq nonzerosLim)
            }.toList()
        }

        datapoints += benchmarks.map { solverEntry ->
            SolverDatapoint(
                rows = solverEntry[MatrixBenchmarkResultTable.rows],
                columns = solverEntry[MatrixBenchmarkResultTable.cols],
                nonzeros = solverEntry[MatrixBenchmarkResultTable.nonzeros],
                solvers = transaction(db) {
                    SolverTable
                        .select { SolverTable.benchmarkId eq solverEntry[MatrixBenchmarkResultTable.id] }
                        .map { solverEntry ->
                            Solver(
                                name = solverEntry[SolverTable.name],
                                completed = solverEntry[SolverTable.completed],
                                recurrentResiduals = RecurrentResidualsTable.select { RecurrentResidualsTable.solverId eq solverEntry[SolverTable.id] }
                                    .map { it[RecurrentResidualsTable.value] },
                                trueResiduals = TrueResidualsTable.select { TrueResidualsTable.solverId eq solverEntry[SolverTable.id] }
                                    .map { it[TrueResidualsTable.value] },
                                implicitResiduals = ImplicitResidualsTable.select { ImplicitResidualsTable.solverId eq solverEntry[SolverTable.id] }
                                    .map { it[ImplicitResidualsTable.value] },
                                iterationTimestamps = IterationTimestampsTable.select { IterationTimestampsTable.solverId eq solverEntry[SolverTable.id] }
                                    .map { it[IterationTimestampsTable.value] },
                                generateTotalTime = solverEntry[SolverTable.generateTime],
                                applyTotalTime = solverEntry[SolverTable.applyTime],
                                applyIterations = solverEntry[SolverTable.applyIterations],
                                applyComponents = SolverApplyComponentTable.select { SolverApplyComponentTable.solverId eq solverEntry[SolverTable.id] }
                                    .map {
                                        Component(
                                            it[SolverApplyComponentTable.name],
                                            it[SolverApplyComponentTable.time]
                                        )
                                    },
                                generateComponents = SolverGenerateComponentTable.select { SolverGenerateComponentTable.solverId eq solverEntry[SolverTable.id] }
                                    .map {
                                        Component(
                                            it[SolverGenerateComponentTable.name],
                                            it[SolverGenerateComponentTable.time]
                                        )
                                    },
                            )
                        }
                }
            )
        }
        return SolverBenchmarkResult(
            commit = commit,
            device = device,
            benchmark = benchmark,
            datapoints = datapoints,
        )
    }
}

