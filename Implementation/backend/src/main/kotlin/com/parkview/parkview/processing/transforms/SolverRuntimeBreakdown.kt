package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.benchmark.SolverDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

data class BarChartDataset(
    val label: String,
    val data: List<Double>,
) : Dataset

class SolverRuntimeBreakdown : SolverPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Bar)
    override val name = "solverRuntimeBreakdown"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "matrix",
            options = results.first().datapoints.map { (it as SolverDatapoint).name }
        ),
        PlotOption(
            name = "components",
            options = listOf("apply", "generate")
        ),
    )


    override fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val datapoint = benchmarkResults.first().datapoints.find { it.name == options["matrix"] }
            ?: throw InvalidPlotTransformException("${options["matrix"]} is not a valid matrix name")

        val seriesByName: MutableMap<String, MutableList<Double>> = mutableMapOf()
        val allComponentNames = datapoint.solvers.fold(emptyList<String>()) { acc, e ->
            acc + when (options["components"]) {
                "apply" -> e.applyComponents.map { it.name }
                "generate" -> e.generateComponents.map { it.name }
                else -> throw InvalidPlotTransformException("${options["components"]} is not a valid option for components")
            }
        }.toSet().toList()

        val labels: MutableList<String> = mutableListOf()
        for (solver in datapoint.solvers) {
            if (!solver.completed) continue

            labels += solver.name

            val components = when (options["components"]) {
                "apply" -> solver.applyComponents
                "generate" -> solver.generateComponents
                else -> throw InvalidPlotTransformException("${options["components"]} is not a valid option for components")
            }
            val totalTime = when (options["components"]) {
                "apply" -> solver.applyComponents.sumOf { it.runtime }
                "generate" -> solver.generateComponents.sumOf { it.runtime }
                else -> throw InvalidPlotTransformException("${options["components"]} is not a valid option for components")
            }
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

}