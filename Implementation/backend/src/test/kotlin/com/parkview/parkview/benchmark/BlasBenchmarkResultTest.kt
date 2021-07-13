package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class BlasBenchmarkResultTest {
    private lateinit var br: BlasBenchmarkResult

    @BeforeEach
    fun setup() {
        val datapoints = (1..5).map {
            BlasDatapoint(
                10, operations = listOf(
                    Operation("", 1.0, 1.0, it * 1.0, true),
                )
            )
        }

        val commit = Commit("", "", Date(), "")
        br = BlasBenchmarkResult(
            commit,
            Device(""),
            Benchmark("", BenchmarkType.Blas),
            datapoints
        )
    }


    @Test
    fun `Test summary value for normal data`() {
        val summaryValue = br.getSummaryValue()

        val medianBandwidth = 3.0

        assert(summaryValue[""] == medianBandwidth)
    }
}