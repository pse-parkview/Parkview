package com.parkview.parkview

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.junit.jupiter.api.Test
import java.io.File

internal class UtilTest {
    @Test
    fun `test json deserialization for single entry`() {
        // TODO use class loader instead of relative path
        // testJson = this.javaClass::class.java.getResource("test_single_spmv.json").readText()
        val path = "src/test/resources/test_single_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)
        val points = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        println(points.toJson())
        // TODO: actual assertion
    }

    @Test
    fun `test json deserialization for multiple entries`() {
        // TODO use class loader instead of relative path
        // testJson = this.javaClass::class.java.getResource("test_single_spmv.json").readText()
        val path = "src/test/resources/test_multiple_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResult = Util.benchmarkResultFromJson(testJson, BenchmarkType.SpmvBenchmark)
        val points = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time, 443, 443).transform(listOf(benchmarkResult as SpmvBenchmarkResult))
        print(points.toJson())

        // TODO: actual assertion
    }
}