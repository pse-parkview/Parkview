package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.transforms.matrix.MatrixOptions

abstract class MatrixPlotTransform : PlotTransform {
    abstract fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption>

    final override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> =
        getMatrixPlotOptions(results) + listOf(
            MatrixOptions.minRows,
            MatrixOptions.maxRows,
            MatrixOptions.maxColumns,
            MatrixOptions.minColumns,
            MatrixOptions.minNonzeros,
            MatrixOptions.maxNonzeros,
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
