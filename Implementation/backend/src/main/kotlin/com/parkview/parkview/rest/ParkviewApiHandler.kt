package com.parkview.parkview.rest

import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.parkview.parkview.git.RepositoryHandler
import com.parkview.parkview.processing.AvailablePlots
import com.parkview.parkview.processing.AveragePerformanceCalculator
import com.parkview.parkview.processing.PlotDescription
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlottableData
import kotlin.js.Date

class ParkviewApiHandler(
    private val repHandler: RepositoryHandler,
    private val databaseHandler: DatabaseHandler,
) : RestHandler {
    private val performanceCalculator = AveragePerformanceCalculator(databaseHandler)

    override fun getHistory(
        branch: String,
        page: Int,
        benchmark: String,
    ): List<Commit> = repHandler.fetchGitHistoryByBranch(branch, page, BenchmarkType.valueOf(benchmark))

    override fun getPlot(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
        plotType: String,
        plotParams: Map<String, String>,
    ): PlottableData {
        val benchmarkType = BenchmarkType.valueOf(benchmark)
        val results: List<BenchmarkResult> = shas.zip(devices).map { (sha, device) ->
            databaseHandler.fetchBenchmarkResult(
                Commit(sha, "", Date(), ""),
                Device(device),
                benchmarkType,
            )
        }

        val plot =
            AvailablePlots.getPlotByName(plotType, benchmarkType) ?: throw IllegalArgumentException("Invalid plot type")
        val config = PlotConfiguration(plot.getPlotDescription(results), plotParams)
        return plot.transform(results, config)
    }

    override fun getAvailableBranches(): List<String> = repHandler.getAvailableBranches()

    override fun getAvailableBenchmarks(): List<String> = BenchmarkType.values().map { it.toString() }

    override fun getAvailablePlots(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
    ): List<PlotDescription> =
        AvailablePlots.getPlotList(
            BenchmarkType.valueOf(benchmark),
            shas.zip(devices).map { (sha, device) ->
                databaseHandler.fetchBenchmarkResult(
                    Commit(sha),
                    Device(device),
                    BenchmarkType.valueOf(benchmark),
                )
            }
        )

    override fun getSummaryValue(benchmark: String, sha: String, device: String): List<Pair<String, Double>> =
        databaseHandler.fetchBenchmarkResult(
            Commit(sha = sha),
            Device(device),
            BenchmarkType.valueOf(benchmark)
        ).summaryValues.toList()

    override fun getAveragePerformance(branch: String, benchmark: String, device: String): PlottableData {
        val commits = repHandler.fetchGitHistoryByBranch(branch, 1, BenchmarkType.valueOf(benchmark))
        return performanceCalculator.getAveragePerformanceData(
            commits,
            BenchmarkType.valueOf(benchmark),
            Device(device)
        )
    }

    override fun getNumberOfPages(branch: String): Int = repHandler.getNumberOfPages(branch)

    override fun getAvailableDevices(branch: String, benchmark: BenchmarkType): List<Device> =
        repHandler.fetchGitHistoryByBranch(branch, 1, benchmark).map { it.availableDevices }.flatten().toSet().toList()
}
