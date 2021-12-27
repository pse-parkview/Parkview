package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.js.Date

internal class PreconditionerBenchmarkResultTest {
    private lateinit var br: PreconditionerBenchmarkResult

    @BeforeTest
    fun setup() {
        val datapoints = (1..5).map {
            PreconditionerDatapoint(
                "", 10, 10, 10,
                listOf(
                    Preconditioner(
                        "",
                        listOf(
                            Component("", it.toDouble()),
                        ),
                        it.toDouble(),
                        listOf(
                            Component("", it.toDouble())
                        ),
                        it.toDouble(), true
                    )
                )
            )
        }

        val commit = Commit("", "", Date(), "")
        br = PreconditionerBenchmarkResult(
            commit,
            Device(""),
            datapoints
        )
    }

    @Test
    fun Test_summary_value_for_normal_data() {
        val summaryValue = br.summaryValues

        val medianGenerateRuntime = 3.0

        assertEquals(summaryValue[""], medianGenerateRuntime)
    }
}
