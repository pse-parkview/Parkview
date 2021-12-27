package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.js.Date

internal class SolverBenchmarkResultTest {
    private lateinit var br: SolverBenchmarkResult

    @BeforeTest
    fun setup() {
        val datapoints = (1..5).map {
            SolverDatapoint(
                "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10, "",
                listOf(
                    Solver(
                        "",
                        generateComponents = listOf(Component("", 1.0)),
                        generateTotalTime = 1.0,
                        applyComponents = listOf(Component("", 1.0)),
                        applyIterations = it.toLong() * 10,
                        applyTotalTime = 1.0,
                        completed = true
                    )
                )
            )
        }

        val commit = Commit("", "", Date(), "")
        br = SolverBenchmarkResult(
            commit,
            Device(""),
            datapoints
        )
    }

    @Test
    fun get_summary_value_for_single_solver_per_datapoint() {
        val summaryValue = br.summaryValues

        val medianIterations = 30.0

        assertEquals(summaryValue[""], medianIterations)
    }
}
