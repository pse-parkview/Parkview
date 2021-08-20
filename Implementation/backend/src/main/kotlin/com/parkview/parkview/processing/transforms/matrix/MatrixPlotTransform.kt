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
            MatrixOptions.minRows.realizeOption(results),
            MatrixOptions.maxRows.realizeOption(results),
            MatrixOptions.maxColumns.realizeOption(results),
            MatrixOptions.minColumns.realizeOption(results),
            MatrixOptions.minNonzeros.realizeOption(results),
            MatrixOptions.maxNonzeros.realizeOption(results),
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
