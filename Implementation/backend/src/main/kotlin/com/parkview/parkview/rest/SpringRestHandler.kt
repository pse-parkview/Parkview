package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.JsonParser
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.database.ExposedHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler : RestHandler {
    val repHandler = CachingRepositoryHandler(GitApiHandler("ginkgo", "ginkgo-project"))

    @PostMapping("/post")
    override fun handlePost(@RequestBody json: String) {
        val benchmarkResults = JsonParser.benchmarkResultsFromJson(json)

        val databaseHandler = ExposedHandler()
        databaseHandler.updateBenchmarkResults(benchmarkResults)
    }

    @GetMapping("/history")
    override fun handleGetHistory(@RequestParam branch: String, @RequestParam page: Int): String {
//        val repositoryHandler: RepositoryHandler = GitApiHandler("ginkgo", "ginkgo-project")

        val gson = Gson()

        val commits = repHandler.fetchGitHistory(branch, page)
        return gson.toJson(commits)
    }


    @GetMapping("/getPlotData")
    override fun handleGetBenchmarkResults(
        @RequestParam benchmark: String,
        @RequestParam sha: String,
        @RequestParam device: String
    ): String {
        // dummy implementation
        val databaseHandler = ExposedHandler()

        val gson = Gson()
//        val request = gson.fromJson(json, GetPlotDataRequest::class.java)
        val benchmarkResult = databaseHandler.fetchBenchmarkResult(
            Commit(sha, "", Date(), ""),
            Device(device),
            Benchmark(benchmark, BenchmarkType.SpmvBenchmark),
            nonzerosLim = 500000,
        )

        val points =
            SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        return points.toJson()
    }

    @GetMapping("/branches")
    override fun getAvailableBranches(): String {
        val branches = repHandler.getAvailableBranches()
        val gson = Gson()
        return gson.toJson(branches)
    }
}