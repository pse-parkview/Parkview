package com.parkview.parkview

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.benchmark.*
import com.parkview.parkview.git.*
import java.util.*

private data class ProblemModel(
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


// TODO: move this to better fitting class
class JsonParser private constructor() {
    companion object {
        fun benchmarkResultsFromJson(
            sha: String,
            benchmark: String,
            device: String,
            json: String,
            blas: Boolean = false
        ): List<BenchmarkResult> = if (!blas) {
            matrixBenchmarkResultsFromJson(sha, benchmark, device, json)
        } else {
            blasBenchmarkResultFromJson(sha, benchmark, device, json)
        }

        private fun matrixBenchmarkResultsFromJson(
            sha: String,
            benchmark: String,
            device: String,
            json: String
        ): List<BenchmarkResult> {
            val arrayType = object : TypeToken<List<MatrixDatapointModel>>() {}.type

            val datapoints: List<MatrixDatapointModel> = Gson().fromJson(json, arrayType)

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
            val device = Device(device)

            if (spmvDatapoints.isNotEmpty()) results.add(
                SpmvBenchmarkResult(
                    commit = commit,
                    datapoints = spmvDatapoints,
                    device = device,
                    benchmark = Benchmark(benchmark, BenchmarkType.SpmvBenchmark),
                )
            )

            if (conversionDatapoints.isNotEmpty()) results.add(
                ConversionBenchmarkResult(
                    commit = commit,
                    datapoints = conversionDatapoints,
                    device = device,
                    benchmark = Benchmark(benchmark, BenchmarkType.ConversionBenchmark),
                )
            )

            if (solverDatapoints.isNotEmpty()) results.add(
                SolverBenchmarkResult(
                    commit = commit,
                    datapoints = solverDatapoints,
                    device = device,
                    benchmark = Benchmark(benchmark, BenchmarkType.SolverBenchmark),
                )
            )

            if (preconditionerDatapoints.isNotEmpty()) results.add(
                PreconditionerBenchmarkResult(
                    commit = commit,
                    datapoints = preconditionerDatapoints,
                    device = device,
                    benchmark = Benchmark(benchmark, BenchmarkType.PreconditionerBenchmark),
                )
            )

            return results
        }

        private fun blasBenchmarkResultFromJson(
            sha: String,
            benchmark: String,
            device: String,
            json: String
        ): List<BenchmarkResult> {
            val arrayType = object : TypeToken<List<BlasDatapointModel>>() {}.type

            val datapoints: List<BlasDatapointModel> = Gson().fromJson(json, arrayType)

            return listOf(
                BlasBenchmarkResult(
                    commit = Commit(sha, "", Date(), ""),
                    device = Device(device),
                    benchmark = Benchmark(benchmark, BenchmarkType.BlasBenchmark),
                    datapoints = datapoints.map { it.toBlasDatapoint() },
                )
            )
        }
    }

}