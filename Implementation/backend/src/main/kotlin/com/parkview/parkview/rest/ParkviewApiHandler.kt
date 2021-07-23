package com.parkview.parkview.rest

import com.parkview.parkview.benchmark.JsonParser
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.processing.AvailablePlots
import com.parkview.parkview.processing.AveragePerformanceCalculator
import com.parkview.parkview.processing.PlotDescription
import com.parkview.parkview.processing.transforms.PlottableData
import java.util.*

class ParkviewApiHandler(
    private val repHandler: RepositoryHandler,
    private val databaseHandler: DatabaseHandler,
) : RestHandler {
    private val performanceCalculator = AveragePerformanceCalculator(databaseHandler)

    override fun postBenchmarkResults(
        sha: String,
        device: String,
        json: String,
    ) {
        val benchmarkResults = JsonParser.benchmarkResultsFromJson(sha, device, json)

        databaseHandler.insertBenchmarkResults(benchmarkResults)
    }

    override fun getHistory(
        branch: String,
        page: Int,
        benchmark: String,
    ): List<Commit> = repHandler.fetchGitHistory(branch, page, BenchmarkType.valueOf(benchmark))


    override fun getPlot(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
        plotType: String,
        allParams: Map<String, String>,
    ): PlottableData {
        val results: List<BenchmarkResult> = shas.zip(devices).map { (sha, device) ->
            databaseHandler.fetchBenchmarkResult(
                Commit(sha, "", Date(), ""),
                Device(device),
                BenchmarkType.valueOf(benchmark),
            )
        }

        val plot = AvailablePlots.getPlotByName(plotType) ?: throw IllegalArgumentException("Invalid plot type")
        return plot.transform(results, allParams)
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

    override fun getSummaryValue(benchmark: String, sha: String, device: String): Map<String, Double> =
        databaseHandler.fetchBenchmarkResult(Commit(sha = sha),
            Device(device),
            BenchmarkType.valueOf(benchmark)).summaryValues

    override fun getAveragePerformance(branch: String, benchmark: String, device: String): PlottableData {
        val commits = repHandler.fetchGitHistory(branch, 1, BenchmarkType.valueOf(benchmark))
        return performanceCalculator.getAveragePerformanceData(commits,
            BenchmarkType.valueOf(benchmark),
            Device(device))
    }

    override fun getNumberOfPages(branch: String): Int = repHandler.getNumberOfPages(branch)

    override fun getAvailableDevices(branch: String, benchmark: BenchmarkType): List<Device> =
        repHandler.fetchGitHistory(branch, 1, benchmark).map { it.availableDevices }.flatten().toSet().toList()
}