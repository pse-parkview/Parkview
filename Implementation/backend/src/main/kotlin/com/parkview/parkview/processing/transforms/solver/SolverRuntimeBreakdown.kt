package com.parkview.parkview.processing.transforms.solver

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.BarChartDataset
import com.parkview.parkview.processing.transforms.InvalidPlotOptionValueException
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.getAvailableMatrixNames
import com.parkview.parkview.processing.transforms.getComponentsByOption
import com.parkview.parkview.processing.transforms.getOptionValueByName
import com.parkview.parkview.processing.transforms.getTotalTimeByOption

class SolverRuntimeBreakdown : SolverPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Bar)
    override val name = "Runtime Breakdown"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        getAvailableMatrixNames(results.first()),
        CategoricalOption(
            name = "components",
            options = listOf("apply", "generate"),
            description = "Which components to plot"
        ),
        CategoricalOption(
            name = "totalTime",
            options = listOf("sumComponents", "givenValue"),
            description = "Take the total time given in the benchmark or the sum of all runtimes"
        ),
    )

    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val datapoint = benchmarkResults.first().datapoints.find { it.name == options.getOptionValueByName("matrix") }
            ?: throw InvalidPlotOptionValueException(options, "matrix")

        val seriesByName: MutableMap<String, MutableList<Double>> = mutableMapOf()
        val allComponentNames = datapoint.solvers.fold(emptyList<String>()) { acc, e ->
            acc + e.getComponentsByOption(options).map { it.name }
        }.toSet().toList()

        val labels: MutableList<String> = mutableListOf()
        for (solver in datapoint.solvers) {
            if (!solver.completed) continue

            labels += solver.name

            val components = solver.getComponentsByOption(options)
            val totalTime = solver.getTotalTimeByOption(options)
            for (componentName in allComponentNames) {
                seriesByName.getOrPut(componentName) { mutableListOf() } += components.find { it.name == componentName }?.runtime?.div(
                    totalTime
                ) ?: 0.0
            }
        }

        return PlottableData(
            labels = labels,
            datasets = seriesByName.map { BarChartDataset(it.key, it.value) }
        )
    }
}
