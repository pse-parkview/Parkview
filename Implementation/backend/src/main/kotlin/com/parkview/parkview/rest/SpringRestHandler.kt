package com.parkview.parkview.rest

import com.parkview.parkview.Util
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler : RestHandler {
    override fun handlePost(json: String) {
        TODO("Not yet implemented")
    }

    override fun handleGetHistory(json: String) {
        TODO("Not yet implemented")
    }

    @GetMapping("/benchmarkResult")
    override fun handleGetBenchmarkResults(@RequestBody json: String): String {
        // dummy implementation
        val path = "src/test/resources/test_multiple_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)
        val points = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time, 443, 443).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        print(points.toJson())
        return points.toJson()
    }
}