package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.PlotTransform
import com.parkview.parkview.processing.transforms.blas.BlasPlotTransform
import com.parkview.parkview.processing.transforms.blas.BlasSpeedupTransform
import com.parkview.parkview.processing.transforms.blas.SingleBlasPlot
import com.parkview.parkview.processing.transforms.matrix.conversion.ConversionPlotTransform
import com.parkview.parkview.processing.transforms.matrix.conversion.ConversionSingleScatterPlot
import com.parkview.parkview.processing.transforms.matrix.conversion.ConversionSpeedupPlot
import com.parkview.parkview.processing.transforms.matrix.preconditioner.PreconditionerOverview
import com.parkview.parkview.processing.transforms.matrix.preconditioner.PreconditionerPlotTransform
import com.parkview.parkview.processing.transforms.matrix.solver.SolverConvergencePlot
import com.parkview.parkview.processing.transforms.matrix.solver.SolverPlotTransform
import com.parkview.parkview.processing.transforms.matrix.solver.SolverRuntimeBreakdown
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvPerformanceProfile
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvPlotTransform
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSpeedupPlot

/**
 * Singleton that keeps track of all plot types and offers helper functions
 */
object AvailablePlots {
    private val spmvPlots: List<SpmvPlotTransform> = listOf(
        SpmvSingleScatterPlot(),
        SpmvSpeedupPlot(),
        SpmvPerformanceProfile(),
    )
    private val blasPlots: List<BlasPlotTransform> = listOf(
        SingleBlasPlot(),
        BlasSpeedupTransform(),
    )

    private val preconditionerPlots: List<PreconditionerPlotTransform> = listOf(
        PreconditionerOverview(),
    )

    private val conversionPlots: List<ConversionPlotTransform> = listOf(
        ConversionSingleScatterPlot(),
        ConversionSpeedupPlot(),
    )

    private val solverPlots: List<SolverPlotTransform> = listOf(
        SolverConvergencePlot(),
        SolverRuntimeBreakdown(),
    )

    /**
     * Returns a [PlotTransform] for the given name
     *
     * @param plotName name of plot
     *
     * @return [PlotTransform] if plot exists, otherwise null
     */
    fun getPlotByName(plotName: String, benchmarkType: BenchmarkType): PlotTransform? = when (benchmarkType) {
        BenchmarkType.Spmv -> spmvPlots
        BenchmarkType.Solver -> solverPlots
        BenchmarkType.Preconditioner -> preconditionerPlots
        BenchmarkType.Conversion -> conversionPlots
        BenchmarkType.Blas -> blasPlots
    }.find { it.name == plotName }

    /**
     * Returns a list of all available plots for a given number of inputs and benchmark type
     *
     * @param benchmark type of benchmark
     * @param results chosen benchmark results
     *
     * @return list of [PlotDescription] containing the available plots grouped by plot type
     */
    fun getPlotList(benchmark: BenchmarkType, results: List<BenchmarkResult>): List<PlotDescription> {
        val availablePlots: List<PlotTransform> = when (benchmark) {
            BenchmarkType.Spmv -> spmvPlots
            BenchmarkType.Solver -> solverPlots
            BenchmarkType.Preconditioner -> preconditionerPlots
            BenchmarkType.Conversion -> conversionPlots
            BenchmarkType.Blas -> blasPlots
        }.filter { transform -> results.size in transform.numInputsRange }

        return availablePlots.map { it.getPlotDescription(results) }
    }
}
