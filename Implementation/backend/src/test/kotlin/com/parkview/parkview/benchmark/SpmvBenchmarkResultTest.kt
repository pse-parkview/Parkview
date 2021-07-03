package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.util.*

internal class SpmvBenchmarkResultTest {
    private lateinit var br: SpmvBenchmarkResult

    @BeforeEach
    fun setup() {
        val datapoints = (1..5).map {
            val format = Format("", 1, 1.0, 1.0, true)
            SpmvDatapoint(
                it * 10, it * 10, it * 10, listOf(
                    format
                ), format
            )
        }

        val commit = Commit("", "", Date())
        br = SpmvBenchmarkResult(
            commit,
            Device(""),
            Benchmark("", BenchmarkType.ConversionBenchmark),
            datapoints
        )
    }

    @Test
    fun `Get summary value for single format per datapoint`() {
        val summaryValue = br.getSummaryValue()

        val medianBandwidth = 30.0

        assert(summaryValue[""] == medianBandwidth)
    }
}