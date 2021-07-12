package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.JsonParser
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.database.DatabaseHandler
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
    val databaseHandler: DatabaseHandler = ExposedHandler()

    @PostMapping("/post")
    override fun handlePost(
        @RequestParam sha: String,
        @RequestParam device: String,
        @RequestParam benchmark: String,
        @RequestParam(defaultValue = "false") blas: Boolean,
        @RequestBody json: String,
    ) {
        val benchmarkResults = JsonParser.benchmarkResultsFromJson(sha, benchmark, device, json, blas = blas)

        databaseHandler.updateBenchmarkResults(benchmarkResults)
    }

    @GetMapping("/history")
    override fun handleGetHistory(
        @RequestParam branch: String,
        @RequestParam page: Int,
    ): String {
//        val repositoryHandler: RepositoryHandler = GitApiHandler("ginkgo", "ginkgo-project")

        val gson = Gson()

        val commits = repHandler.fetchGitHistory(branch, page)
        return gson.toJson(commits)
    }


    @GetMapping("/plot")
    override fun handleGetPlotData(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
        @RequestParam plotType: String,
    ): String {
        // dummy implementation
        val results: List<BenchmarkResult> = shas.zip(devices).map { (sha, device) ->
            databaseHandler.fetchBenchmarkResult(
                Commit(sha, "", Date(), ""),
                Device(device),
                Benchmark(benchmark, BenchmarkType.SpmvBenchmark),
                nonzerosLim = 5000000,
            )
        }

        println(results)

        return when (plotType) {
            "spmvSingleScatter" -> SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time).transform(results as List<SpmvBenchmarkResult>)
                .toJson()
            else -> ""
        }
    }

    @GetMapping("/branches")
    override fun getAvailableBranches(): String {
        val branches = repHandler.getAvailableBranches()
        val gson = Gson()
        return gson.toJson(branches)
    }
}