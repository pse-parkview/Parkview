package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.DynamicNumericalOption

object BlasOptions {
    /**
     * Most common x axis options for blas benchmarks
     */
    val xAxis = CategoricalOption(
        name = "xAxis",
        options = arrayOf("n", "m", "r", "k"),
        description = "Value displayed on x axis",
    )

    /**
     * Most common y axis options for blas benchmarks
     */
    val yAxis = CategoricalOption(
        name = "yAxis",
        options = arrayOf("time", "flops", "bandwidth"),
        description = "Value displayed on y axis"
    )

    val minN = object : DynamicNumericalOption("minN", "Lower limit for n") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.n }
                .minOrNull()?.toDouble() ?: 0.0
    }

    val maxN = object : DynamicNumericalOption("maxN", "Upper limit for n") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.n }
                .maxOrNull()?.toDouble() ?: 0.0
    }

    val minR = object : DynamicNumericalOption("minR", "Lower limit for r") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.r }
                .minOrNull()?.toDouble() ?: 0.0
    }

    val maxR = object : DynamicNumericalOption("maxR", "Upper limit for r") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.r }
                .maxOrNull()?.toDouble() ?: 0.0
    }

    val minM = object : DynamicNumericalOption("minM", "Lower limit for m") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.m }
                .minOrNull()?.toDouble() ?: 0.0
    }

    val maxM = object : DynamicNumericalOption("maxM", "Upper limit for m") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.m }
                .maxOrNull()?.toDouble() ?: 0.0
    }

    val minK = object : DynamicNumericalOption("minK", "Lower limit for k") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.k }
                .minOrNull()?.toDouble() ?: 0.0
    }

    val maxK = object : DynamicNumericalOption("maxK", "Upper limit for k") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten().map { it.k }
                .maxOrNull()?.toDouble() ?: 0.0
    }
}
