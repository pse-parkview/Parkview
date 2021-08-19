package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.NumericalOption
import com.parkview.parkview.processing.PlotOption

abstract class MatrixPlotTransform : PlotTransform {
    abstract fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption>

    final override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> {
        val datapoints = results.asSequence().filterIsInstance<MatrixBenchmarkResult>().map { it.datapoints }.flatten()
        return getMatrixPlotOptions(results) + listOf<PlotOption>(
            NumericalOption(
                name = "minRows",
                description = "Lower limit for rows",
                default = datapoints.map { it.rows }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxRows",
                description = "Upper limit for rows",
                default = datapoints.map { it.rows }.maxOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "minColumns",
                description = "Lower limit for columns",
                default = datapoints.map { it.columns }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxColumns",
                description = "Upper limit for columns",
                default = datapoints.map { it.columns }.maxOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "minNonzeros",
                description = "Lower limit for nonzeros",
                default = datapoints.map { it.nonzeros }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxNonzeros",
                description = "Upper limit for nonzeros",
                default = datapoints.map { it.nonzeros }.maxOrNull()?.toDouble() ?: 0.0,
            ),
        )
    }
}

fun filterMatrixDatapoints(datapoints: List<MatrixDatapoint>, options: Map<String, String>): List<MatrixDatapoint> {
    val minRows = options.getOptionValueByName("minRows").toDouble()
    val maxRows = options.getOptionValueByName("maxRows").toDouble()
    val minColumns = options.getOptionValueByName("minColumns").toDouble()
    val maxColumns = options.getOptionValueByName("maxColumns").toDouble()
    val minNonzeros = options.getOptionValueByName("minNonzeros").toDouble()
    val maxNonzeros = options.getOptionValueByName("maxNonzeros").toDouble()

    return datapoints
        .filter { (it.rows >= minRows) and (it.rows <= maxRows) }
        .filter { (it.columns >= minColumns) and (it.columns <= maxColumns) }
        .filter { (it.nonzeros >= minNonzeros) and (it.nonzeros <= maxNonzeros) }
}

val MATRIX_X_AXIS = CategoricalOption(
    name = "xAxis",
    options = listOf("nonzeros", "rows", "columns")
)

fun getAvailableMatrixNames(result: BenchmarkResult) = CategoricalOption(
    name = "matrix",
    options = (result as MatrixBenchmarkResult).datapoints.map { it.name }
)
