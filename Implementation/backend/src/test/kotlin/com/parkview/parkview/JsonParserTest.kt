package com.parkview.parkview

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlotYAxis
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.fail

internal class JsonParserTest {
    @Test
    fun `test json deserialization for single spmv entry`() {
        // TODO use class loader instead of relative path
        // testJson = this.javaClass::class.java.getResource("test_single_spmv.json").readText()
        val path = "src/test/resources/test_single_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResults = JsonParser.benchmarkResultsFromJson(testJson)
        assert(benchmarkResults.size == 1)
        val result = benchmarkResults.first()
        if (result !is SpmvBenchmarkResult) fail("invalid type, should be SpmvBenchmarkResult")
        assert(result.datapoints.size == 1)
        assert(result.datapoints[0].rows == 1138L)
        assert(result.datapoints[0].columns == 1138L)
        assert(result.datapoints[0].nonzeros == 4054L)
        assert(result.datapoints[0].formats.size == 5)
        assert(result.datapoints[0].formats[0].time == 46033550.0)
    }

    @Test
    fun `test json deserialization for big file`() {
        // TODO use class loader instead of relative path
        // testJson = this.javaClass::class.java.getResource("test_single_spmv.json").readText()
        val path = "src/test/resources/test_multiple_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResults = JsonParser.benchmarkResultsFromJson(testJson)
        val points = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Time, 443, 443).transform(benchmarkResults as List<SpmvBenchmarkResult>)
        println(points.toJson())

        // TODO: actual assertion
    }

    @Test
    fun `test json deserialization for file containing spmv and conversion types`() {
        val path = "src/test/resources/test_single_conversion_spmv.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResults = JsonParser.benchmarkResultsFromJson(testJson)
        assert(benchmarkResults.size == 2)
        assert((benchmarkResults[0] is ConversionBenchmarkResult) xor (benchmarkResults[1] is ConversionBenchmarkResult))
        assert((benchmarkResults[0] is SpmvBenchmarkResult) xor (benchmarkResults[1] is SpmvBenchmarkResult))
    }

    @Test
    fun `test json deserialization for file containing solver benchmark type`() {
        val path = "src/test/resources/test_single_solver.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResults = JsonParser.benchmarkResultsFromJson(testJson)
        assert(benchmarkResults.size == 1)
        val result = benchmarkResults.first()
        if (result !is SolverBenchmarkResult) fail("invalid type, should be SolverBenchmarkResult")
        assert(result.datapoints.size == 1)
        assert(result.datapoints.first().solvers.size == 6)
    }

    @Test
    fun `test json deserialization for file containing single blas benchmark type`() {
        val path = "src/test/resources/test_single_blas.json"
        val file = File(path)
        val testJson = file.readText()

        val benchmarkResults = JsonParser.benchmarkResultsFromJson(testJson, blas = true)
        assert(benchmarkResults.size == 1)
        val result = benchmarkResults.first()
        if (result !is BlasBenchmarkResult) fail("invalid type, should be BlasBenchmarkResult")
        assert(result.datapoints.size == 1)
        assert(result.datapoints.first().operations.size == 6)
        assert(result.datapoints.first().n == 100L)
        assert(result.datapoints.first().k == 100L)
        assert(result.datapoints.first().r == 1L)
    }
}