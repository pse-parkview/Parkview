package com.parkview.parkview.processing.transforms.solver

import com.parkview.parkview.benchmark.Solver
import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.BarChartDataset
import com.parkview.parkview.processing.transforms.InvalidPlotConfigValueException
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.getAvailableMatrixNames

class SolverRuntimeBreakdown : SolverPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Bar)
    override val name = "Runtime Breakdown"

    private val componentsOption = CategoricalOption(
        name = "components",
        options = listOf("apply", "generate"),
        description = "Which components to plot"
    )

    private val totalTimeOption = CategoricalOption(
        name = "totalTime",
        options = listOf("sumComponents", "givenValue"),
        description = "Take the total time given in the benchmark or the sum of all runtimes"
    )

    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        getAvailableMatrixNames(results.first()),
        componentsOption,
        totalTimeOption,
    )

    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData {
        val datapoint = benchmarkResults.first().datapoints.find { it.name == config.getCategoricalOption("matrix") }
            ?: throw InvalidPlotConfigValueException(config.getCategoricalOption("matrix"), "matrix")

        val seriesByName: MutableMap<String, MutableList<Double>> = mutableMapOf()
        val allComponentNames = datapoint.solvers.fold(emptyList<String>()) { acc, e ->
            acc + e.getComponentsByOption(config).map { it.name }
        }.toSet().toList()

        val labels: MutableList<String> = mutableListOf()
        for (solver in datapoint.solvers) {
            if (!solver.completed) continue

            labels += solver.name

            val components = solver.getComponentsByOption(config)
            val totalTime = solver.getTotalTimeByOption(config)
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

    private fun Solver.getComponentsByOption(config: PlotConfiguration) =
        when (config.getCategoricalOption(componentsOption)) {
            "apply" -> this.applyComponents
            "generate" -> this.generateComponents
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(componentsOption),
                componentsOption.name
            )
        }

    private fun Solver.getTotalTimeByOption(config: PlotConfiguration) =
        when (config.getCategoricalOption(componentsOption)) {
            "apply" -> this.getApplyTotalTimeByOption(config)
            "generate" -> this.getGenerateTotalTimeByOption(config)
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(componentsOption),
                componentsOption.name
            )
        }

    private fun Solver.getGenerateTotalTimeByOption(config: PlotConfiguration) =
        when (config.getCategoricalOption(totalTimeOption)) {
            "sumComponents" -> this.generateComponents.sumOf { it.runtime }
            "givenValue" -> this.generateTotalTime
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(totalTimeOption),
                totalTimeOption.name
            )
        }

    private fun Solver.getApplyTotalTimeByOption(config: PlotConfiguration) =
        when (config.getCategoricalOption(totalTimeOption)) {
            "sumComponents" -> this.applyComponents.sumOf { it.runtime }
            "givenValue" -> this.applyTotalTime
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(totalTimeOption),
                totalTimeOption.name
            )
        }
}
