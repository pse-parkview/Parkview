package com.parkview.parkview.benchmark

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
        val datapoints = (1..5).map {
            ConversionDatapoint(
                it.toLong() * 10, it.toLong() * 10, it.toLong() * 10, listOf(
                    Conversion("", 1.0, true),
                )
            )
        }

        val commit = Commit("", "", Date(), "")
        br = ConversionBenchmarkResult(
            commit,
            Device(""),
            BenchmarkType.Conversion,
            datapoints
        )
    }

    @Test
    fun `test summary value for single conversion in each datapoint`() {
        val summaryValue = br.getSummaryValue()

        val medianBandwidth = 30.0

        assert(summaryValue[""] == medianBandwidth)
    }
}