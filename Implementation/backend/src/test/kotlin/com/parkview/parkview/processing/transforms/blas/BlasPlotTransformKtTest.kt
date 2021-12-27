package com.parkview.parkview.processing.transforms.blas

import BLAS_RESULT
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.filterBlasDatapoints
import kotlin.test.Test
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
    fun filter_blas_datapoints() {
        val options = mapOf(
            "maxN" to "10",
            "minN" to "1",
            "maxR" to "20",
            "minR" to "2",
            "maxM" to "30",
            "minM" to "3",
            "maxK" to "40",
            "minK" to "4",
            "xAxis" to "n",
            "yAxis" to "time",
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
        nOptions["maxN"] = "7"
        nOptions["minN"] = "5"
        val nFiltered = filterBlasDatapoints(datapoints, PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), nOptions))
        assertNull(nFiltered.find { it.n < 5 })
        assertNull(nFiltered.find { it.n > 7 })

        val rOptions = options.toMutableMap()
        rOptions["maxR"] = "10"
        rOptions["minR"] = "12"
        val rFiltered = filterBlasDatapoints(datapoints, PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), rOptions))
        assertNull(rFiltered.find { it.r < 10 })
        assertNull(rFiltered.find { it.r > 12 })

        val kOptions = options.toMutableMap()
        kOptions["maxK"] = "0"
        kOptions["minK"] = "3"
        val kFiltered = filterBlasDatapoints(datapoints, PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), kOptions))
        assertNull(kFiltered.find { it.k < 0 })
        assertNull(kFiltered.find { it.k > 3 })

        val mOptions = options.toMutableMap()
        mOptions["maxM"] = "32"
        mOptions["minM"] = "40"
        val mFiltered = filterBlasDatapoints(datapoints, PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), mOptions))
        assertNull(mFiltered.find { it.m < 32 })
        assertNull(mFiltered.find { it.m > 40 })
    }
}
