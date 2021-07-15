package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class SpmvBenchmarkResultTest {
    private lateinit var br: SpmvBenchmarkResult

    @BeforeEach
    fun setup() {
        val datapoints = (1..5).map {
            val format = Format(name = "", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
            SpmvDatapoint(
                it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
                listOf(
                    format
                ),
            )
        }

        val commit = Commit("", "", Date(), "")
        br = SpmvBenchmarkResult(
            commit,
            Device(""),
            BenchmarkType.Spmv,
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