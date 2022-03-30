package com.parkview.parkview.processing.transforms.matrix.solver

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.InvalidPlotConfigValueException
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset
import com.parkview.parkview.processing.transforms.matrix.MatrixOptions

class SolverConvergencePlot : SolverPlotTransform() {
    override val numInputsRange: IntRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Line)
    override val name: String = "Convergence Plot"

    private val xAxisOption = CategoricalOption(
        name = "xAxis",
        options = arrayOf("iteration_timestamps", "array_index"),
        description = "Value that gets displayed on the x axis",
    )

    private val yAxisOption = CategoricalOption(
        name = "yAxis",
        options = arrayOf("recurrent_residuals", "true_residuals", "implicit_residuals"),
        description = "Value that gets displayed on the y axis",
    )

    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MatrixOptions.matrix.realizeOption(results.toTypedArray()),
        xAxisOption,
        yAxisOption,
    )

    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData {
        val benchmarkResult = benchmarkResults.firstOrNull()
            ?: throw InvalidPlotTransformException("Empty list of BenchmarkResult passed")

        val datapoint = benchmarkResult.datapoints.find {
            it.name == config.getCategoricalOption(MatrixOptions.matrix)
        } ?: throw InvalidPlotConfigValueException(
            config.getCategoricalOption(MatrixOptions.matrix),
            MatrixOptions.matrix.name
        )

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (solver in datapoint.solvers) {
            val wantedResiduals = when (config.getCategoricalOption(yAxisOption)) {
                "recurrent_residuals" -> solver.recurrentResiduals
                "true_residuals" -> solver.trueResiduals
                "implicit_residuals" -> solver.implicitResiduals
                else -> throw InvalidPlotConfigValueException(
                    config.getCategoricalOption(yAxisOption),
                    yAxisOption.name
                )
            }

            val wantedXAxis = when (config.getCategoricalOption(xAxisOption)) {
                "iteration_timestamps" -> solver.iterationTimestamps
                "array_index" -> (0..(wantedResiduals.size)).map { it.toDouble() }.toList()
                else -> throw InvalidPlotConfigValueException(
                    config.getCategoricalOption(xAxisOption),
                    xAxisOption.name
                )
            }

            seriesByName.getOrPut(solver.name) { mutableListOf() } += wantedResiduals.zip(wantedXAxis)
                .filter { !it.first.isNaN() and !it.second.isNaN() }.map {
                    PlotPoint(
                        x = it.second,
                        y = it.first,
                    )
                }
        }

        return PlottableData(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }.toTypedArray()) }.toTypedArray()
        )
    }
}
