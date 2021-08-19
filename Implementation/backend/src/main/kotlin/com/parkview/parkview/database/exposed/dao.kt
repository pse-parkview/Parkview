package com.parkview.parkview.database.exposed

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.benchmark.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

private val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

/**
 * DAO for general benchmark data
 */
class BenchmarkResultRow(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BenchmarkResultRow>(BenchmarkResultTable)

    /**
     * Sha of the commit the benchmark has been run on
     */
    var sha by BenchmarkResultTable.sha

    /**
     * Name of the benchmark (one of the names of BenchmarkType)
     */
    var name by BenchmarkResultTable.name

    /**
     * Device the benchmark has been run on
     */
    var device by BenchmarkResultTable.device
}

/**
 * DAO for matrix datapoints in general
 */
abstract class MatrixDatapointRow(id: EntityID<UUID>, matrixTable: MatrixDatapointTable) : UUIDEntity(id) {
    /**
     * Reference to the benchmark this datapoint is part of
     */
    var benchmark by BenchmarkResultRow referencedOn matrixTable.benchmarkId
    /**
     * Name of the matrix
     */
    var name by matrixTable.name

    /**
     * Number of columns
     */
    var cols by matrixTable.cols

    /**
     * Number of rows
     */
    var rows by matrixTable.rows

    /**
     * Number of nonzeros
     */
    var nonzeros by matrixTable.nonzeros
}

/**
 * DAO for Spmv datapoints
 */
class SpmvDatapointRow(id: EntityID<UUID>) : MatrixDatapointRow(id, SpmvDatapointTable) {
    companion object : UUIDEntityClass<SpmvDatapointRow>(SpmvDatapointTable)

    private val type = object : TypeToken<List<Format>>() {}.type

    /**
     * Formats that have been run for the datapoint
     */
    var formats: List<Format> by SpmvDatapointTable.data.transform({ gson.toJson(it) }, { gson.fromJson(it, type) })

    /**
     * Converts the DAO to a [SpmvDatapoint]
     *
     * @return the [SpmvDatapoint] stored in this row
     */
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

    /**
     * Conversions that have been run for the datapoint
     */
    var conversions: List<Conversion> by ConversionDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    /**
     * Converts the DAO to a [ConversionDatapoint]
     *
     * @return the [ConversionDatapoint] stored in this row
     */
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

    /**
     * Preconditioners that have been run for the datapoint
     */
    var preconditioners: List<Preconditioner> by PreconditionerDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    /**
     * Converts the DAO to a [PreconditionerDatapoint]
     *
     * @return the [PreconditionerDatapoint] stored in this row
     */
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

    /**
     * Optimal Spmv for this benchmark
     */
    var optimal by SolverDatapointTable.optimal

    private val type = object : TypeToken<List<Solver>>() {}.type

    /**
     * Solvers that have been run for the datapoint
     */
    var solvers: List<Solver> by SolverDatapointTable.data.transform({ gson.toJson(it) }, { gson.fromJson(it, type) })

    /**
     * Converts the DAO to a [SolverDatapoint]
     *
     * @return the [SolverDatapoint] stored in this row
     */
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

    /**
     * Reference to the benchmark this datapoint is part of
     */
    var benchmark by BenchmarkResultRow referencedOn BlasDatapointTable.benchmarkId

    /**
     * N value for this benchmark
     */
    var n by BlasDatapointTable.n

    /**
     * N value for this benchmark
     */
    var r by BlasDatapointTable.r

    /**
     * N value for this benchmark
     */
    var k by BlasDatapointTable.k

    /**
     * N value for this benchmark
     */
    var m by BlasDatapointTable.m

    private val type = object : TypeToken<List<Operation>>() {}.type

    /**
     * Operations that have been run for the datapoint
     */
    var operations: List<Operation> by BlasDatapointTable.data.transform({ gson.toJson(it) },
        { gson.fromJson(it, type) })

    /**
     * Converts the DAO to a [BlasDatapoint]
     *
     * @return the [BlasDatapoint] stored in this row
     */
    fun toBlasDatapoint() = BlasDatapoint(
        n = n,
        r = r,
        m = m,
        k = k,
        operations = operations,
    )
}
