package com.parkview.parkview.processing.transforms.matrix

import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.transforms.InvalidPlotConfigValueException
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotTransform

abstract class MatrixPlotTransform : PlotTransform {
    abstract fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption>

    final override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> =
        getMatrixPlotOptions(results) + listOf(
            MatrixOptions.minRows.realizeOption(results.toTypedArray()),
            MatrixOptions.maxRows.realizeOption(results.toTypedArray()),
            MatrixOptions.maxColumns.realizeOption(results.toTypedArray()),
            MatrixOptions.minColumns.realizeOption(results.toTypedArray()),
            MatrixOptions.minNonzeros.realizeOption(results.toTypedArray()),
            MatrixOptions.maxNonzeros.realizeOption(results.toTypedArray()),
        )

    protected fun MatrixDatapoint.getXAxisByConfig(config: PlotConfiguration) =
        when (config.getCategoricalOption(MatrixOptions.xAxis)) {
            "nonzeros" -> nonzeros.toDouble()
            "rows" -> rows.toDouble()
            "columns" -> columns.toDouble()
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(MatrixOptions.xAxis),
                MatrixOptions.xAxis.name
            )
        }
}
