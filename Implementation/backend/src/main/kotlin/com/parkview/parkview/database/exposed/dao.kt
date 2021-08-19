package com.parkview.parkview.database.exposed

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.benchmark.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

private val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

class BenchmarkResultRow(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BenchmarkResultRow>(BenchmarkResultTable)

    var sha by BenchmarkResultTable.sha
    var name by BenchmarkResultTable.name
    var device by BenchmarkResultTable.device
}

abstract class MatrixDatapointRow(id: EntityID<UUID>, matrixTable: MatrixDatapointTable) : UUIDEntity(id) {
    var benchmark by BenchmarkResultRow referencedOn matrixTable.benchmarkId
    var name by matrixTable.name
    var cols by matrixTable.cols
    var rows by matrixTable.rows
    var nonzeros by matrixTable.nonzeros
}

class SpmvDatapointRow(id: EntityID<UUID>) : MatrixDatapointRow(id, SpmvDatapointTable) {
    companion object : UUIDEntityClass<SpmvDatapointRow>(SpmvDatapointTable)

    private val type = object : TypeToken<List<Format>>() {}.type

    var formats: List<Format> by SpmvDatapointTable.data.transform({ gson.toJson(it) }, { gson.fromJson(it, type) })

    fun toSpmvDatapoint() = SpmvDatapoint(
        name = name,
        columns = cols,
        rows = rows,
        nonzeros = nonzeros,
        formats = formats,
    )
}

class ConversionDatapointRow(id: EntityID<UUID>) : MatrixDatapointRow(id, ConversionDatapointTable) {
    companion object : UUIDEntityClass<ConversionDatapointRow>(ConversionDatapointTable)

    private val type = object : TypeToken<List<Conversion>>() {}.type

    var conversions: List<Conversion> by ConversionDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    fun toConversionDatapoint() = ConversionDatapoint(
        name = name,
        columns = cols,
        rows = rows,
        nonzeros = nonzeros,
        conversions = conversions,
    )
}

class PreconditionerDatapointRow(id: EntityID<UUID>) : MatrixDatapointRow(id, PreconditionerDatapointTable) {
    companion object : UUIDEntityClass<PreconditionerDatapointRow>(PreconditionerDatapointTable)

    private val type = object : TypeToken<List<Preconditioner>>() {}.type

    var preconditioners: List<Preconditioner> by PreconditionerDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    fun toPreconditionerDatapoint() = PreconditionerDatapoint(
        name = name,
        columns = cols,
        rows = rows,
        nonzeros = nonzeros,
        preconditioners = preconditioners,
    )
}

class SolverDatapointRow(id: EntityID<UUID>) : MatrixDatapointRow(id, SolverDatapointTable) {
    companion object : UUIDEntityClass<SolverDatapointRow>(SolverDatapointTable)

    var optimal by SolverDatapointTable.optimal

    private val type = object : TypeToken<List<Solver>>() {}.type

    var solvers: List<Solver> by SolverDatapointTable.data.transform({ gson.toJson(it) }, { gson.fromJson(it, type) })

    fun toSolverDatapoint() = SolverDatapoint(
        name = name,
        columns = cols,
        rows = rows,
        nonzeros = nonzeros,
        solvers = solvers,
        optimal = optimal
    )
}

class BlasDatapointRow(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BlasDatapointRow>(BlasDatapointTable)

    var benchmark by BenchmarkResultRow referencedOn BlasDatapointTable.benchmarkId
    var n by BlasDatapointTable.n
    var r by BlasDatapointTable.r
    var k by BlasDatapointTable.k
    var m by BlasDatapointTable.m
    var data by BlasDatapointTable.data

    private val type = object : TypeToken<List<Operation>>() {}.type
    var operations: List<Operation> by BlasDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    fun toBlasDatapoint() = BlasDatapoint(
        n = n,
        r = r,
        m = m,
        k = k,
        operations = operations,
    )
}
