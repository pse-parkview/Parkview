package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.processing.transforms.InvalidPlotOptionValueException
import com.parkview.parkview.processing.transforms.filterBlasDatapoints
import com.parkview.parkview.processing.transforms.getXAxisByOption
import com.parkview.parkview.processing.transforms.getYAxisByOption
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNull

internal class BlasPlotTransformKtTest {
    private val datapoint = BlasDatapoint(
        1,
        2,
        3,
        4,
        listOf(Operation("name", 1.0, 2.0, 3.0, true, 4)),
    )

    @Test
    fun getYAxisByOption() {
        val operation = datapoint.operations.first()

        assertEquals(operation.getYAxisByOption(mapOf("yAxis" to "time")), 1.0)
        assertEquals(operation.getYAxisByOption(mapOf("yAxis" to "flops")), 2.0)
        assertEquals(operation.getYAxisByOption(mapOf("yAxis" to "bandwidth")), 3.0)
        assertThrows<InvalidPlotOptionValueException> {
            operation.getYAxisByOption(mapOf())
        }
    }

    @Test
    fun getXAxisByOption() {
        assertEquals(datapoint.getXAxisByOption(mapOf("xAxis" to "n")), 1)
        assertEquals(datapoint.getXAxisByOption(mapOf("xAxis" to "r")), 2)
        assertEquals(datapoint.getXAxisByOption(mapOf("xAxis" to "m")), 3)
        assertEquals(datapoint.getXAxisByOption(mapOf("xAxis" to "k")), 4)
        assertThrows<InvalidPlotOptionValueException> {
            datapoint.getXAxisByOption(mapOf())
        }
    }

    @Test
    fun `filter blas datapoints`() {
        val options = mapOf(
            "maxN" to 10,
            "minN" to 1,
            "maxR" to 20,
            "minR" to 2,
            "maxM" to 30,
            "minM" to 3,
            "maxK" to 40,
            "minK" to 4,
        )

        val datapoints = (1..10).map {
            BlasDatapoint(
                it.toLong() * 1,
                it.toLong() * 2,
                it.toLong() * 3,
                it.toLong() * 4,
                emptyList()
            )
        }

        val nOptions = options.toMutableMap()
        nOptions["maxN"] = 7
        nOptions["minN"] = 5
        val nFiltered = filterBlasDatapoints(datapoints, nOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(nFiltered.find { it.n < 5 })
        assertNull(nFiltered.find { it.n > 7 })

        val rOptions = options.toMutableMap()
        rOptions["maxR"] = 10
        rOptions["minR"] = 12
        val rFiltered = filterBlasDatapoints(datapoints, rOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(rFiltered.find { it.r < 10 })
        assertNull(rFiltered.find { it.r > 12 })

        val kOptions = options.toMutableMap()
        kOptions["maxK"] = 0
        kOptions["minK"] = 3
        val kFiltered = filterBlasDatapoints(datapoints, kOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(kFiltered.find { it.k < 0 })
        assertNull(kFiltered.find { it.k > 3 })

        val mOptions = options.toMutableMap()
        mOptions["maxM"] = 32
        mOptions["minM"] = 40
        val mFiltered = filterBlasDatapoints(datapoints, mOptions.map { it.key to it.value.toString() }.toMap())
        assertNull(mFiltered.find { it.m < 32 })
        assertNull(mFiltered.find { it.m > 40 })
    }
}
