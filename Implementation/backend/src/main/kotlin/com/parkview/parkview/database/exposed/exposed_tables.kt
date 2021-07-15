package com.parkview.parkview.database.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

object BenchmarkTypeTable : Table() {
    override val tableName: String = "BenchmarkType"
    val name: Column<String> = varchar("name", 30).uniqueIndex()
    val format: Column<String> = varchar("format", 30)
    override val primaryKey = PrimaryKey(BenchmarkTypeTable.name, name = "PK_BenchmarkType_Name")
}

object MatrixBenchmarkResultTable : Table() {
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

object ConversionTable : Table() {
    override val tableName = "Conversion"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val benchmarkId: Column<UUID> = reference("benchmarkId", MatrixBenchmarkResultTable.id)
    val name: Column<String> = varchar("name", 40)
    val completed: Column<Boolean> = bool("completed")
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(ConversionTable.id, name = "PK_Conversion_Id")
}

object SpmvFormatTable : Table() {
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

object BlasBenchmarkResultTable : Table() {
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

object BlasOperationTable : Table() {
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

object SolverTable : Table() {
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

object SolverGenerateComponentTable : Table() {
    override val tableName: String = "SolverGenerateComponent"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val name: Column<String> = varchar("name", 40)
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_SolverGenerateComponent_Id")
}

object SolverApplyComponentTable : Table() {
    override val tableName: String = "SolverApplyComponent"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val name: Column<String> = varchar("name", 40)
    val time: Column<Double> = double("time")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_SolverApplyComponent_Id")
}

object RecurrentResidualsTable : Table() {
    override val tableName: String = "RecurrentResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_RecurrentResiduals_Id")
}

object TrueResidualsTable : Table() {
    override val tableName: String = "TrueResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_TrueResiduals_Id")
}

object ImplicitResidualsTable : Table() {
    override val tableName: String = "ImplicitResiduals"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_ImplicitResiduals_Id")
}

object IterationTimestampsTable : Table() {
    override val tableName: String = "IterationTimestamps"
    val id: Column<UUID> = uuid("id").autoGenerate().uniqueIndex()
    val solverId: Column<UUID> = reference("solverId", SolverTable.id)
    val value: Column<Double> = double("value")
    override val primaryKey = PrimaryKey(SolverGenerateComponentTable.id, name = "PK_IterationTimestamps_Id")
}

