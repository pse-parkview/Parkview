package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.js.Date

internal class BlasBenchmarkResultTest {
    private lateinit var br: BlasBenchmarkResult

    @BeforeTest
    fun setup() {
        val datapoints = (1..5).map {
            BlasDatapoint(
                10,
                operations = listOf(
                    Operation("", 1.0, 1.0, it * 1.0, true),
                )
            )
        }

        val commit = Commit("", "", Date(), "")
        br = BlasBenchmarkResult(
            commit,
            Device(""),
            datapoints
        )
    }

    @Test
    fun test_summary_value_for_normal_data() {
        val summaryValue = br.summaryValues

        val medianBandwidth = 3.0

        assertEquals(summaryValue[""], medianBandwidth)
    }
}
