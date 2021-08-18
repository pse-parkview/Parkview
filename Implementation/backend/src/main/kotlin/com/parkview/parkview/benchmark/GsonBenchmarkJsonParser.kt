package com.parkview.parkview.benchmark

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import java.util.*

private data class ProblemModel(
    val group: String = "",
    val name: String = "",
    val cols: Long = 0,
    val rows: Long = 0,
    val nonzeros: Long = 0,
)

private data class FormatModel(
    val completed: Boolean = false,
    val time: Double = 0.0,
)

private data class ConversionModel(
    val completed: Boolean = false,
    val time: Double = 0.0,
)

private data class SolverModel(
    @SerializedName("recurrent_residuals") val recurrentResiduals: List<Double>?,
    @SerializedName("true_residuals") val trueResiduals: List<Double>?,
    @SerializedName("implicit_residuals") val implicitResiduals: List<Double>?,
    @SerializedName("iteration_timestamps") val iterationTimestamps: List<Double>?,
    @SerializedName("rhs_norm") val rhsNorm: Double,
    val generate: GenerateModel?,
    val apply: ApplyModel?,
    @SerializedName("residual_norm") val residualNorm: Double?,
    val completed: Boolean,
)

private data class GenerateModel(
    val components: Map<String, Double>,
    val time: Double,
)

private data class ApplyModel(
    val components: Map<String, Double>,
    val time: Double,
    val iterations: Long?,
)

private data class PreconditionerModel(
    val generate: GenerateModel?,
    val apply: ApplyModel?,
    val completed: Boolean,
)

private data class OptimalModel(
    val spmv: String
)

private data class DatapointModel(
    val n: Long?,
    val r: Long? = 1,
    val m: Long? = n,
    val k: Long? = n,
    val blas: Map<String, BlasOperationModel>,
    val problem: ProblemModel,
    val spmv: Map<String, FormatModel>?,
    val conversions: Map<String, ConversionModel>?,
    val solver: Map<String, SolverModel>?,
    val preconditioner: Map<String, PreconditionerModel>?,
    val optimal: OptimalModel?,
) {
    fun toBlasDatapoint(): BlasDatapoint? = if ((blas != null) and (n != null)) {
        BlasDatapoint(
            n!!,
            r ?: 1,
            m ?: n,
            k ?: n,
            blas.map { (key, value) ->
                Operation(
                    key,
                    value.time,
                    value.flops,
                    value.bandwidth,
                    value.completed,
                    value.repetitions ?: 0
                )
            }
        )
    } else {
        null
    }

    // this means solver and spmv can not be in the same datapoint
    fun toSpmvDatapoint(): SpmvDatapoint? = if ((spmv != null) and (solver == null)) {
        SpmvDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            spmv?.map { (key, value) -> Format(key, value.time, value.completed) }?.toList() ?: emptyList()
        )
    } else {
        null
    }

    fun toConversionDatapoint(): ConversionDatapoint? = if (conversions != null) {
        ConversionDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            conversions.map { (key, value) -> Conversion(key, value.time, value.completed) }.toList()
        )
    } else {
        null
    }

    fun toSolverDatapoint(): SolverDatapoint? = if ((solver != null) and (optimal != null)) {
        SolverDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            optimal!!.spmv,
            solver!!.map { (key, value) ->
                Solver(
                    key,
                    value.recurrentResiduals ?: emptyList(),
                    value.trueResiduals ?: emptyList(),
                    value.implicitResiduals ?: emptyList(),
                    value.iterationTimestamps ?: emptyList(),
                    value.rhsNorm,
                    value.residualNorm ?: 0.01,
                    value.completed,
                    value.generate?.components?.map { (key, value) -> Component(key, value) } ?: emptyList(),
                    value.generate?.time ?: -1.0,
                    value.apply?.components?.map { (key, value) -> Component(key, value) } ?: emptyList(),
                    value.apply?.time ?: 0.0,
                    value.apply?.iterations ?: 0
                )
            }.toList()
        )
    } else {
        null
    }

    fun toPreconditionerDatapoint(): PreconditionerDatapoint? = if (preconditioner != null) {
        PreconditionerDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            preconditioner.map { (key, value) ->
                Preconditioner(
                    key,
                    value.generate?.components?.map { Component(it.key, it.value) } ?: emptyList(),
                    value.generate?.time ?: -1.0,
                    value.apply?.components?.map { Component(it.key, it.value) } ?: emptyList(),
                    value.apply?.time ?: 0.0,
                    value.completed,
                )
            }
        )
    } else {
        null
    }
}

private data class BlasOperationModel(
    val time: Double,
    val flops: Double,
    val bandwidth: Double,
    val completed: Boolean,
    val repetitions: Long?,
)

/**
 * Class that takes care of parsing the json representation used in ginkgo-data to [BenchmarkResult] objects
 */
class GsonBenchmarkJsonParser : BenchmarkJsonParser {

    override fun benchmarkResultsFromJson(
        sha: String,
        deviceName: String,
        json: String,
    ): List<BenchmarkResult> {
        val arrayType = object : TypeToken<List<DatapointModel>>() {}.type

        val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()
        val datapoints: List<DatapointModel> = gson.fromJson(json, arrayType)

        val spmvDatapoints: MutableList<SpmvDatapoint> = mutableListOf()
        val conversionDatapoints: MutableList<ConversionDatapoint> = mutableListOf()
        val solverDatapoints: MutableList<SolverDatapoint> = mutableListOf()
        val preconditionerDatapoints: MutableList<PreconditionerDatapoint> = mutableListOf()
        val blasDatapoints: MutableList<BlasDatapoint> = mutableListOf()

        for (datapoint in datapoints) {
            val spmvDatapoint = datapoint.toSpmvDatapoint()
            if (spmvDatapoint != null) spmvDatapoints.add(spmvDatapoint)

            val conversionDatapoint = datapoint.toConversionDatapoint()
            if (conversionDatapoint != null) conversionDatapoints.add(conversionDatapoint)

            val solverDatapoint = datapoint.toSolverDatapoint()
            if (solverDatapoint != null) solverDatapoints.add(solverDatapoint)

            val preconditionerDatapoint = datapoint.toPreconditionerDatapoint()
            if (preconditionerDatapoint != null) preconditionerDatapoints.add(preconditionerDatapoint)

            val blasDatapoint = datapoint.toBlasDatapoint()
            if (blasDatapoint != null) blasDatapoints.add(blasDatapoint)
        }

        val results: MutableList<BenchmarkResult> = mutableListOf()
        val commit = Commit(sha, "", Date(), "")
        val device = Device(deviceName)

        if (spmvDatapoints.isNotEmpty()) results.add(
            SpmvBenchmarkResult(
                commit = commit,
                datapoints = spmvDatapoints,
                device = device,
                benchmark = BenchmarkType.Spmv,
            )
        )

        if (conversionDatapoints.isNotEmpty()) results.add(
            ConversionBenchmarkResult(
                commit = commit,
                datapoints = conversionDatapoints,
                device = device,
                benchmark = BenchmarkType.Conversion,
            )
        )

        if (solverDatapoints.isNotEmpty()) results.add(
            SolverBenchmarkResult(
                commit = commit,
                datapoints = solverDatapoints,
                device = device,
                benchmark = BenchmarkType.Solver,
            )
        )

        if (preconditionerDatapoints.isNotEmpty()) results.add(
            PreconditionerBenchmarkResult(
                commit = commit,
                datapoints = preconditionerDatapoints,
                device = device,
                benchmark = BenchmarkType.Preconditioner,
            )
        )

        if (blasDatapoints.isNotEmpty()) results.add(
            BlasBenchmarkResult(
                commit = commit,
                datapoints = blasDatapoints,
                device = device,
                benchmark = BenchmarkType.Blas,
            )
        )

        return results
    }
}