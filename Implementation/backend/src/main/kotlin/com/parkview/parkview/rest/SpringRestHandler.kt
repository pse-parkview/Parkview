package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.Util
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.database.ExposedHandler
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.RepositoryHandler
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File

private data class GetBenchmarkResultRequest(
    val yaxis: String,
    val rows: Int,
    val cols: Int,
    val benchmark: String,
)

private data class GetHistoryRequest(
    val branch: String,
)

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler : RestHandler {
    @PostMapping("/post")
    override fun handlePost(@RequestBody json: String) {
        println(json.subSequence(0, 30))
        val benchmarkResult = Util.benchmarkResultFromJson(json, BenchmarkType.SpmvBenchmark)
//        val path = "src/test/resources/test_multiple_spmv.json"
//        val file = File(path)
//        val testJson = file.readText()
//        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)

        val databaseHandler = ExposedHandler()
        databaseHandler.updateBenchmarkResults(listOf(benchmarkResult))
    }

    @GetMapping("/history")
    override fun handleGetHistory(@RequestBody json: String): String {
        val repositoryHandler: RepositoryHandler = GitApiHandler("ginkgo", "ginkgo-project")

        val gson = Gson()
        val request = gson.fromJson(json, GetHistoryRequest::class.java)

        val commits = repositoryHandler.fetchGitHistory(request.branch)
        return gson.toJson(commits)
    }


    @GetMapping("/benchmarkResult")
    override fun handleGetBenchmarkResults(@RequestBody json: String): String {
        // dummy implementation
        val path = "src/test/resources/test_multiple_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)
        val points = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        return points.toJson()
    }
}