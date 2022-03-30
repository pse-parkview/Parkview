package com.parkview.parkview.processing.transforms.matrix

import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.DynamicCategoricalOption
import com.parkview.parkview.processing.DynamicNumericalOption
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException

object MatrixOptions {
    val matrix = object : DynamicCategoricalOption("matrix", "Which matrix to plot") {
        override fun getOptions(results: Array<BenchmarkResult>): Array<String> = if (results.size == 1)
            (results.first() as MatrixBenchmarkResult).datapoints.map { it.name }.toTypedArray()
        else throw InvalidPlotTransformException("Plots using single matrices are only available for single benchmarks")
    }

    /**
     * Most common X axis options for matrix benchmarks
     */
    val xAxis = CategoricalOption(
        name = "xAxis",
        options = arrayOf("nonzeros", "rows", "columns"),
        description = "Value displayed on the x axis",
    )

    val minRows = object : DynamicNumericalOption("minRows", "Lower limit for rows") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten().map { it.rows }
                .minOrNull()?.toDouble() ?: 0.0
    }

    val maxRows = object : DynamicNumericalOption("maxRows", "Upper limit for rows") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten().map { it.rows }
                .maxOrNull()?.toDouble() ?: 0.0
    }

    val minColumns = object : DynamicNumericalOption("minColumns", "Lower limit for columns") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten()
                .map { it.columns }.minOrNull()?.toDouble() ?: 0.0
    }

    val maxColumns = object : DynamicNumericalOption("maxColumns", "Upper limit for columns") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten()
                .map { it.columns }.maxOrNull()?.toDouble() ?: 0.0
    }

    val minNonzeros = object : DynamicNumericalOption("minNonzeros", "Lower limit for nonzeros") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten()
                .map { it.nonzeros }.minOrNull()?.toDouble() ?: 0.0
    }

    val maxNonzeros = object : DynamicNumericalOption("maxNonzeros", "Upper limit for nonzeros") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten()
                .map { it.nonzeros }.maxOrNull()?.toDouble() ?: 0.0
    }
}
