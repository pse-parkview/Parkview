package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

internal class SolverBenchmarkResultTest {
    private lateinit var br: SolverBenchmarkResult

    @BeforeEach
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
    fun `Get summary value for single solver per datapoint`() {
        val summaryValue = br.summaryValues

        val medianIterations = 30.0

        assert(summaryValue[""] == medianIterations)
    }
}
