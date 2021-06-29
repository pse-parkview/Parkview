package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class ConversionBenchmarkResultTest {
    private lateinit var br: ConversionBenchmarkResult

    @BeforeEach
    fun setup() {
        val d1 = ConversionDatapoint("", 10, 10, 10, listOf(
            Conversion("", 1.0, true),
        ))
        val d2 = ConversionDatapoint("", 20, 20, 20, listOf(
            Conversion("", 1.0, true),
        ))
        val d3 = ConversionDatapoint("", 30, 30, 30, listOf(
            Conversion("", 1.0, true),
        ))

        val commit = Commit("", "", Date())
        br = ConversionBenchmarkResult(
            commit,
            Device(""),
            Benchmark("", BenchmarkType.ConversionBenchmark),
            listOf(d1, d2, d3)
        )
    }

    @Test
    fun `test summary value for normal values`() {
        val summaryValue = br.getSummaryValue()

        val medianBandwidth = 20.0

        assert(summaryValue == medianBandwidth)
    }
}