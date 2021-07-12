package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.Util
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.database.ExposedHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.*

private data class GetPlotDataRequest(
//    val yaxis: String,
//    val rows: Int,
//    val cols: Int,
    val benchmark: String,
    val sha: String,
    val device: String,
)

private data class GetHistoryRequest(
    val branch: String,
    val page: Int,
)

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler : RestHandler {
    val repHandler = CachingRepositoryHandler(GitApiHandler("ginkgo", "ginkgo-project"))

    @PostMapping("/post")
    override fun handlePost(@RequestBody json: String) {
//        println(json.subSequence(0, 30))
        val benchmarkResult = Util.benchmarkResultFromJson(json, BenchmarkType.SpmvBenchmark)
//        val path = "src/test/resources/test_multiple_spmv.json"
//        val file = File(path)
//        val testJson = file.readText()
//        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)

        val databaseHandler = ExposedHandler()
        databaseHandler.updateBenchmarkResults(listOf(benchmarkResult))
    }

    @GetMapping("/history")
    override fun handleGetHistory(@RequestParam branch: String, @RequestParam page: Int): String {
//        val repositoryHandler: RepositoryHandler = GitApiHandler("ginkgo", "ginkgo-project")

        val gson = Gson()

        val commits = repHandler.fetchGitHistory(branch, page)
        return gson.toJson(commits)
    }


    @GetMapping("/getPlotData")
    override fun handleGetBenchmarkResults(@RequestParam benchmark: String, @RequestParam sha: String, @RequestParam device: String): String {
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

//        val path = "src/test/resources/test_multiple_spmv.json"
//        val file = File(path)
//        val testJson = file.readText()
//
//        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)
        val points =
            SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        return points.toJson()
    }

    @GetMapping("/branches")
    override fun getAvailableBranches(): String {
        // val repositoryHandler: RepositoryHandler = GitApiHandler("ginkgo", "ginkgo-project")

        val branches = repHandler.getAvailableBranches()
        val gson = Gson()
        return gson.toJson(branches)
    }
}