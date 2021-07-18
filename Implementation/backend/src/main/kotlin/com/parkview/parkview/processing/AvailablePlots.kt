package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.*

data class PlotList(
    val line: List<PlotDescription>,
    val scatter: List<PlotDescription>,
    val bar: List<PlotDescription>,
    val stackedBar: List<PlotDescription>,
)

data class PlotDescription(
    val plotName: String,
    val options: List<PlotOption>,
)

data class PlotOption(
    val name: String,
    val options: List<String> = emptyList(),
    val number: Boolean = options.isEmpty(),
)

/**
 * Singleton that keeps track of all plot types and offers helper functions
 */
object AvailablePlots {
    private val spmvPlots: List<SpmvPlotTransform> = listOf(
        SpmvSingleScatterPlot(),
        SpmvSpeedupPlot(),
    )
    private val blasPlots: List<BlasPlotTransform> = listOf()

    private val preconditionerPlots: List<PreconditionerPlotTransform> = listOf()

    private val conversionPlots: List<ConversionPlotTransform> = listOf(
        ConversionSingleScatterPlot(),
        ConversionSpeedupPlot(),
    )

    private val solverPlots: List<SolverPlotTransform> = listOf()

    /**
     * Returns a [PlotTransform] for the given name
     *
     * @param plotName name of plot
     *
     * @return [PlotTransform] if plot exists, otherwise null
     */
    fun getPlotByName(plotName: String): PlotTransform? {
        if (spmvPlots.find { it.name == plotName } != null) return spmvPlots.find { it.name == plotName }
        if (blasPlots.find { it.name == plotName } != null) return blasPlots.find { it.name == plotName }
        if (solverPlots.find { it.name == plotName } != null) return solverPlots.find { it.name == plotName }
        if (conversionPlots.find { it.name == plotName } != null) return conversionPlots.find { it.name == plotName }
        if (preconditionerPlots.find { it.name == plotName } != null) return spmvPlots.find { it.name == plotName }

        return null
    }

    /**
     * Returns a list of all available plots for a given number of inputs and benchmark type
     *
     * @param benchmark type of benchmark
     * @param numberInputs number of inputs
     *
     * @return [PlotList] containing the available plots grouped by plot type
     */
    fun getPlotList(benchmark: BenchmarkType, numberInputs: Int): PlotList {
        val availablePlots: List<PlotTransform> = when (benchmark) {
            BenchmarkType.Spmv -> spmvPlots
            BenchmarkType.Solver -> solverPlots
            BenchmarkType.Preconditioner -> preconditionerPlots
            BenchmarkType.Conversion -> conversionPlots
            BenchmarkType.Blas -> blasPlots
        }.filter { transform -> numberInputs in transform.numInputsRange }

        return PlotList(
            line = availablePlots
                .filter { transform -> transform.plottableAs.contains(PlotType.Line) }
                .map { PlotDescription(it.name, it.availableOptions) },
            scatter = availablePlots
                .filter { transform -> transform.plottableAs.contains(PlotType.Scatter) }
                .map { PlotDescription(it.name, it.availableOptions) },
            bar = availablePlots
                .filter { transform -> transform.plottableAs.contains(PlotType.Bar) }
                .map { PlotDescription(it.name, it.availableOptions) },
            stackedBar = availablePlots
                .filter { transform -> transform.plottableAs.contains(PlotType.StackedBar) }
                .map { PlotDescription(it.name, it.availableOptions) },
        )
    }
}