package com.parkview.parkview.database.exposed

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*


object BenchmarkResultTable : UUIDTable() {
    override val tableName: String = "BenchmarkResult"
    val sha: Column<String> = varchar("sha", 40)
    val name: Column<String> = varchar("name", 40)
    val device: Column<String> = varchar("deviceName", 40)
    override val primaryKey = PrimaryKey(id, name = "PK_MatrixBenchmarkResult_Id")
}

abstract class MatrixDatapointTable(name: String) : UUIDTable() {
    override val tableName: String = "${name}Datapoint"
    val benchmarkId: Column<EntityID<UUID>> = reference("benchmarkId", BenchmarkResultTable.id)
    val name: Column<String> = text("name")
    val cols: Column<Long> = long("cols")
    val rows: Column<Long> = long("rows")
    val nonzeros: Column<Long> = long("nonzeros")
    val data: Column<String> = text("data")
    override val primaryKey = PrimaryKey(BenchmarkResultTable.id, name = "PK_${name}Datapoint_Id")
}

object SpmvDatapointTable : MatrixDatapointTable("Spmv")

object ConversionDatapointTable : MatrixDatapointTable("Conversion")

object PreconditionerDatapointTable : MatrixDatapointTable("Preconditioner")

object SolverDatapointTable : MatrixDatapointTable("Solver") {
    val optimal: Column<String> = varchar("optimal", 100)
}

object BlasDatapointTable : UUIDTable() {
    override val tableName: String = "BlasDatapoint"
    val benchmarkId: Column<EntityID<UUID>> = reference("benchmarkId", BenchmarkResultTable.id)
    val n: Column<Long> = long("n")
    val r: Column<Long> = long("r")
    val m: Column<Long> = long("m")
    val k: Column<Long> = long("k")
    val data: Column<String> = text("data")
    override val primaryKey = PrimaryKey(BenchmarkResultTable.id, name = "PK_BlasDatapoint_Id")
}
