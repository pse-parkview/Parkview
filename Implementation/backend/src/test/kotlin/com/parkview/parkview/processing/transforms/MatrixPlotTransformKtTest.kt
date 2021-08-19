package com.parkview.parkview.processing.transforms

import COMMIT_A
import DEVICE
import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MatrixPlotTransformKtTest {
    class DummyMatrixDatapoint(
        override val name: String,
        override val rows: Long,
        override val columns: Long,
        override val nonzeros: Long,
    ) : MatrixDatapoint

    class DummyMatrixBenchmarkResult(
        override val datapoints: List<MatrixDatapoint>,
        override val commit: Commit,
        override val device: Device,
        override val benchmark: BenchmarkType,
        override val summaryValues: Map<String, Double> = emptyMap(),
    ) : MatrixBenchmarkResult

    private val datapoints = (1..10).map {
        DummyMatrixDatapoint(
            it.toString(),
            it.toLong(),
            it.toLong() * 2,
            it.toLong() * 3,
        )
    }

    @Test
    fun filterMatrixDatapoints() {
        val options = mapOf(
            "minRows" to 1,
            "maxRows" to 10,
            "minColumns" to 2,
            "maxColumns" to 20,
            "minNonzeros" to 3,
            "maxNonzeros" to 30,
        )

        val rowOptions = options.toMutableMap()
        rowOptions["minRows"] = 2
        rowOptions["maxRows"] = 9
        val rowFiltered = filterMatrixDatapoints(datapoints, rowOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(rowFiltered.find { it.rows < 2 })
        assertNull(rowFiltered.find { it.rows > 9 })

        val columnsOptions = options.toMutableMap()
        columnsOptions["minColumns"] = 10
        columnsOptions["maxColumns"] = 18
        val columnsFiltered =
            filterMatrixDatapoints(datapoints, columnsOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(columnsFiltered.find { it.columns < 10 })
        assertNull(columnsFiltered.find { it.columns > 18 })

        val nonzerosOptions = options.toMutableMap()
        nonzerosOptions["minNonzeros"] = 15
        nonzerosOptions["maxNonzeros"] = 21
        val nonzerosFiltered =
            filterMatrixDatapoints(datapoints, nonzerosOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(nonzerosFiltered.find { it.nonzeros < 15 })
        assertNull(nonzerosFiltered.find { it.nonzeros > 21 })
    }

    @Test
    fun getAvailableMatrixNames() {
        val commit = DummyMatrixBenchmarkResult(
            datapoints,
            COMMIT_A,
            DEVICE,
            BenchmarkType.Spmv,
        )
        val options = getAvailableMatrixNames(commit)

        for ((i, option) in options.options.withIndex()) {
            assertEquals(option, (i + 1).toString())
        }
    }
}