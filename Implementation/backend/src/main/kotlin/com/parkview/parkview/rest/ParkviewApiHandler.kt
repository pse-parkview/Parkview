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
    ): Array<Commit> = repHandler.fetchGitHistoryByBranch(branch, page, BenchmarkType.valueOf(benchmark)).toTypedArray()

    override fun getPlot(
        benchmark: String,
        shas: Array<String>,
        devices: Array<String>,
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

    override fun getAvailableBranches(): Array<String> = repHandler.getAvailableBranches().toTypedArray()

    override fun getAvailableBenchmarks(): Array<String> = BenchmarkType.values().map { it.toString() }.toTypedArray()

    override fun getAvailablePlots(
        benchmark: String,
        shas: Array<String>,
        devices: Array<String>,
    ): Array<PlotDescription> {
        val shasAndDevices = mutableListOf<Pair<String, String>>()

        // this is incredibly ugly but necessary because zip doesn't work and throws a
        // "TypeError: $receiver.iterator is not a function" error
        for (i in 0..shas.size - 1)
            shasAndDevices.add(Pair(shas[i], devices[i]))

        console.log(shas)
        console.log(shas.size)

        console.log(
            databaseHandler.fetchBenchmarkResult(
                Commit(shas[0]),
                Device(devices[0]),
                BenchmarkType.valueOf(benchmark),
            )
        )
        return AvailablePlots.getPlotList(
            BenchmarkType.valueOf(benchmark),
            shasAndDevices.map { (sha, device) ->
                databaseHandler.fetchBenchmarkResult(
                    Commit(sha),
                    Device(device),
                    BenchmarkType.valueOf(benchmark),
                )
            }
        ).toTypedArray()
    }

    override fun getSummaryValue(benchmark: String, sha: String, device: String): Array<Pair<String, Double>> =
        databaseHandler.fetchBenchmarkResult(
            Commit(sha = sha),
            Device(device),
            BenchmarkType.valueOf(benchmark)
        ).summaryValues.toList().toTypedArray()

    override fun getAveragePerformance(branch: String, benchmark: String, device: String): PlottableData {
        val commits = repHandler.fetchGitHistoryByBranch(branch, 1, BenchmarkType.valueOf(benchmark))
        return performanceCalculator.getAveragePerformanceData(
            commits,
            BenchmarkType.valueOf(benchmark),
            Device(device)
        )
    }

    override fun getNumberOfPages(branch: String): Int = repHandler.getNumberOfPages(branch)

    override fun getAvailableDevices(branch: String, benchmark: BenchmarkType): Array<Device> =
            repHandler.fetchGitHistoryByBranch(branch, 1, benchmark).map { it.availableDevices }.map { it.toList() }.flatten().toSet().toTypedArray()
}
