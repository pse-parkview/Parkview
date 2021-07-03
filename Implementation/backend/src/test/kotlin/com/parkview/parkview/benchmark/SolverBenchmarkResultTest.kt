package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

internal class SolverBenchmarkResultTest {
    private lateinit var br: SolverBenchmarkResult

    @BeforeEach
    fun setup() {
        val datapoints = (1..5).map {
            SolverDatapoint(
                it * 10, it * 10, it * 10, listOf(
                    Solver(
                        "",
                        generateComponents = listOf(Component("", 1.0)),
                        generateTotalTime = 1.0,
                        applyComponents = listOf(Component("", 1.0)),
                        applyIterations = it * 10,
                        applyTotalTime = 1.0,
                        completed = true
                    )
                )
            )
        }

        val commit = Commit("", "", Date())
        br = SolverBenchmarkResult(
            commit,
            Device(""),
            Benchmark("", BenchmarkType.ConversionBenchmark),
            datapoints
        )
    }

    @Test
    fun `Get summary value for single solver per datapoint`() {
        val summaryValue = br.getSummaryValue()

        val medianIterations = 30.0

        assert(summaryValue[""] == medianIterations)
    }
}