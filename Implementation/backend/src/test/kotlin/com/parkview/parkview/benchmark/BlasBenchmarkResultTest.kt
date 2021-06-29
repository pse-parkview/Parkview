package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

internal class BlasBenchmarkResultTest {
    private lateinit var br: BlasBenchmarkResult

    @BeforeEach
    fun setup() {
        val d1 = BlasDatapoint(10, operations = listOf(
            Operation("", 1.0, 1.0, 1.0, true),
        ))
        val d2 = BlasDatapoint(10, operations = listOf(
            Operation("", 1.0, 1.0, 2.0, true),
        ))
        val d3 = BlasDatapoint(10, operations = listOf(
            Operation("", 1.0, 1.0, 3.0, true),
        ))

        val commit = Commit("", "", Date())
        br = BlasBenchmarkResult(
            commit,
            Device(""),
            Benchmark("", BenchmarkType.BlasBenchmark),
            listOf(d1, d2, d3)
        )
    }


    @Test
    fun `Test summary value for normal data`() {
        val summaryValue = br.getSummaryValue()

        val medianBandwidth = 2.0

        assert(summaryValue == medianBandwidth)
    }
}