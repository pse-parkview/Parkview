package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class SolverConvergencePlot : SolverPlotTransform {
    override val numInputsRange: IntRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Line)
    override val name: String = "solverConvergence"

    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "yAxis",
            options = listOf("recurrent_residuals", "true_residuals", "implicit_residuals"),
        ),
        PlotOption(
            name = "xAxis",
            options = listOf("iteration_timestamps", "array_index"),
        ),
        PlotOption(
            name = "datapoint",
            options = (results.first() as MatrixBenchmarkResult).datapoints.map { it.name }
        ),
    )

    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val benchmarkResult = benchmarkResults.firstOrNull()
            ?: throw InvalidPlotTransformException("Empty list of BenchmarkResult passed")
        val datapoint = benchmarkResult.datapoints.first {
            it.name == options["datapoint"]
        }


        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (solver in datapoint.solvers) {
            val wantedResiduals = when (options["yAxis"]) {
                "recurrent_residuals" -> solver.recurrentResiduals
                "true_residuals" -> solver.trueResiduals
                "implicit_residuals" -> solver.implicitResiduals
                else -> throw InvalidPlotOptionsException(options, "xAxis")
            }

            val wantedXAxis = when (options["xAxis"]) {
                "iteration_timestamps" -> solver.iterationTimestamps
                "array_index" -> (0..(wantedResiduals.size)).map { it.toDouble() }.toList()
                else -> throw InvalidPlotOptionsException(options, "yAxis")
            }

            seriesByName.getOrPut(solver.name) { mutableListOf() } += wantedResiduals.zip(wantedXAxis)
                .filter { !it.first.isNaN() and !it.second.isNaN() }.map {
                    PlotPoint(
                        x = it.second,
                        y = it.first,
                    )
                }
        }


        return DatasetSeries(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}