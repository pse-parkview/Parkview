package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
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
