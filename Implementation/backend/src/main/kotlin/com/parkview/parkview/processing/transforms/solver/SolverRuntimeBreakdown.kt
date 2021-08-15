package com.parkview.parkview.processing.transforms.solver

import com.parkview.parkview.benchmark.Solver
import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class SolverRuntimeBreakdown : SolverPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Bar)
    override val name = "solverRuntimeBreakdown"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        getAvailableMatrixNames(results),
        CategoricalOption(
            name = "components",
            options = listOf("apply", "generate")
        ),
        CategoricalOption(
            name = "totalTime",
            options = listOf("sumComponents", "givenValue")
        ),
    )

    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val datapoint = benchmarkResults.first().datapoints.find { it.name == options["matrix"] }
            ?: throw InvalidPlotOptionsException(options, "matrix")

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

        return DatasetSeries(
            labels = labels,
            datasets = seriesByName.map { BarChartDataset(it.key, it.value) }
        )
    }

    private fun Solver.getComponentsByOption(options: Map<String, String>) = when (options["components"]) {
        "apply" -> this.applyComponents
        "generate" -> this.generateComponents
        else -> throw InvalidPlotOptionsException(options, "components")
    }

    private fun Solver.getTotalTimeByOption(options: Map<String, String>) = when (options["components"]) {
        "apply" -> this.getApplyTotalTimeByOption(options)
        "generate" -> this.getGenerateTotalTimeByOption(options)
        else -> throw InvalidPlotOptionsException(options, "components")
    }

    private fun Solver.getGenerateTotalTimeByOption(options: Map<String, String>) = when (options["totalTime"]) {
        "sumComponents" -> this.generateComponents.sumOf { it.runtime }
        "givenValue" -> this.generateTotalTime
        else -> throw InvalidPlotOptionsException(options, "totalTime")
    }

    private fun Solver.getApplyTotalTimeByOption(options: Map<String, String>) = when (options["totalTime"]) {
        "sumComponents" -> this.applyComponents.sumOf { it.runtime }
        "givenValue" -> this.applyTotalTime
        else -> throw InvalidPlotOptionsException(options, "totalTime")
    }
}