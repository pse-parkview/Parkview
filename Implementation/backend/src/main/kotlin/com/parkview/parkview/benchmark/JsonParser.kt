package com.parkview.parkview.benchmark

import com.google.gson.Gson
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
    val generate: GenerateModel,
    val apply: ApplyModel,
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
    val generate: GenerateModel,
    val apply: ApplyModel,
    val completed: Boolean,
)

private data class MatrixDatapointModel(
    val problem: ProblemModel,
    val spmv: Map<String, FormatModel>?,
    val conversions: Map<String, ConversionModel>?,
    val solver: Map<String, SolverModel>?,
    val preconditioner: Map<String, PreconditionerModel>?,
) {
    fun toSpmvDatapoint(): SpmvDatapoint? = if (spmv != null) {
        SpmvDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            spmv.map { (key, value) -> Format(key, value.time, value.completed) }.toList()
        )
    } else {
        // TODO find a better way than this
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

    fun toSolverDatapoint(): SolverDatapoint? = if (solver != null) {
        SolverDatapoint(
            problem.group + "/" + problem.name,
            problem.rows,
            problem.cols,
            problem.nonzeros,
            solver.map { (key, value) ->
                Solver(
                    key,
                    value.recurrentResiduals ?: emptyList(),
                    value.trueResiduals ?: emptyList(),
                    value.implicitResiduals ?: emptyList(),
                    value.iterationTimestamps ?: emptyList(),
                    value.rhsNorm,
                    value.residualNorm ?: 0.01,
                    value.completed,
                    value.generate.components.map { (key, value) -> Component(key, value) },
                    value.generate.time,
                    value.apply.components.map { (key, value) -> Component(key, value) },
                    value.apply.time,
                    value.apply.iterations ?: 0
                )
            }.toList()
        )
    } else {
        null
    }

    fun toPreconditionerDatapoint(): PreconditionerDatapoint? {
        return null
    }
}

private data class BlasDatapointModel(
    val n: Long,
    val r: Long? = 1,
    val m: Long? = n,
    val k: Long? = n,
    val blas: Map<String, BlasOperationModel>,
) {
    fun toBlasDatapoint(): BlasDatapoint = BlasDatapoint(
        n,
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
}

private data class BlasOperationModel(
    val time: Double,
    val flops: Double,
    val bandwidth: Double,
    val completed: Boolean,
    val repetitions: Long?,
)

/**
 * Singleton that takes care of parsing the json representation used in ginkgo-data to [BenchmarkResult] objects
 */
object JsonParser {
    /**
     * Converts a json list of objects to a list of benchmark results
     *
     * @param sha sha for commit these benchmarks have been run on
     * @param device device these benchmarks have been run on
     * @param json json as a string
     * @param blas flag whether or not the benchmark is in blas formatthe
     *
     * @return list of [BenchmarkResult]
     */
    fun benchmarkResultsFromJson(
        sha: String,
        device: String,
        json: String,
        blas: Boolean = false
    ): List<BenchmarkResult> = if (!blas) {
        matrixBenchmarkResultsFromJson(sha, device, json)
    } else {
        blasBenchmarkResultFromJson(sha, device, json)
    }

    private fun matrixBenchmarkResultsFromJson(
        sha: String,
        deviceName: String,
        json: String
    ): List<BenchmarkResult> {
        val arrayType = object : TypeToken<List<MatrixDatapointModel>>() {}.type

        val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()
        val datapoints: List<MatrixDatapointModel> = gson.fromJson(json, arrayType)

        val spmvDatapoints: MutableList<SpmvDatapoint> = mutableListOf()
        val conversionDatapoints: MutableList<ConversionDatapoint> = mutableListOf()
        val solverDatapoints: MutableList<SolverDatapoint> = mutableListOf()
        val preconditionerDatapoints: MutableList<PreconditionerDatapoint> = mutableListOf()

        for (datapoint in datapoints) {
            val spmvDatapoint = datapoint.toSpmvDatapoint()
            if (spmvDatapoint != null) spmvDatapoints.add(spmvDatapoint)

            val conversionDatapoint = datapoint.toConversionDatapoint()
            if (conversionDatapoint != null) conversionDatapoints.add(conversionDatapoint)

            val solverDatapoint = datapoint.toSolverDatapoint()
            if (solverDatapoint != null) solverDatapoints.add(solverDatapoint)

            val preconditionerDatapoint = datapoint.toPreconditionerDatapoint()
            if (preconditionerDatapoint != null) preconditionerDatapoints.add(preconditionerDatapoint)
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

        return results
    }

    private fun blasBenchmarkResultFromJson(
        sha: String,
        device: String,
        json: String
    ): List<BenchmarkResult> {
        val arrayType = object : TypeToken<List<BlasDatapointModel>>() {}.type

        val datapoints: List<BlasDatapointModel> = Gson().fromJson(json, arrayType)

        return listOf(
            BlasBenchmarkResult(
                commit = Commit(sha, "", Date(), ""),
                device = Device(device),
                benchmark = BenchmarkType.Blas,
                datapoints = datapoints.map { it.toBlasDatapoint() },
            )
        )
    }
}