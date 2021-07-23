package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.benchmark.JsonParser
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.processing.AvailablePlots
import com.parkview.parkview.processing.AveragePerformanceCalculator
import java.util.*

class ParkviewApiHandler(
    private val repHandler: RepositoryHandler,
    private val databaseHandler: DatabaseHandler,
) : RestHandler {
    private val performanceCalculator = AveragePerformanceCalculator(databaseHandler)

    override fun postBenchmarkResults(
        sha: String,
        device: String,
        blas: Boolean,
        json: String,
    ) {
        val benchmarkResults = JsonParser.benchmarkResultsFromJson(sha, device, json, blas = blas)

        databaseHandler.insertBenchmarkResults(benchmarkResults)
    }

    override fun getHistory(
        branch: String,
        page: Int,
        benchmark: String,
    ): String {
        val commits = repHandler.fetchGitHistory(branch, page, BenchmarkType.valueOf(benchmark))
        return Gson().toJson(commits)
    }


    override fun getPlot(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
        plotType: String,
        allParams: Map<String, String>,
    ): String {
        val results: List<BenchmarkResult> = shas.zip(devices).map { (sha, device) ->
            databaseHandler.fetchBenchmarkResult(
                Commit(sha, "", Date(), ""),
                Device(device),
                BenchmarkType.valueOf(benchmark),
            )
        }

        val plot = AvailablePlots.getPlotByName(plotType) ?: throw IllegalArgumentException("Invalid plot type")
        return plot.transform(results, allParams).toJson()
    }

    override fun getAvailableBranches(): String {
        val branches = repHandler.getAvailableBranches()
        val gson = Gson()
        return gson.toJson(branches)
    }

    override fun getAvailableBenchmarks(): String {
        val benchmarks = BenchmarkType.values()
        return Gson().toJson(benchmarks)
    }

    override fun getAvailablePlots(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
    ): String = Gson().toJson(
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
    )

    override fun getSummaryValue(
        benchmark: String,
        sha: String,
        device: String,
    ): String {
        val result =
            databaseHandler.fetchBenchmarkResult(Commit(sha = sha), Device(device), BenchmarkType.valueOf(benchmark))

        val gson = Gson()
        return gson.toJson(result.summaryValues)
    }

    override fun getAveragePerformance(
        branch: String,
        benchmark: String,
    ): String {
        val commits = repHandler.fetchGitHistory(branch, 1, BenchmarkType.valueOf(benchmark))
        return performanceCalculator.getAveragePerformanceData(commits, BenchmarkType.valueOf(benchmark)).toJson()
    }

    override fun getNumberOfPages(
        branch: String,
    ): String = Gson().toJson(repHandler.getNumberOfPages(branch))
}