package com.parkview.parkview.processing.transforms.blas

import BLAS_RESULT
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.filterBlasDatapoints
import com.parkview.parkview.processing.transforms.getXAxisByConfig
import com.parkview.parkview.processing.transforms.getYAxisByOption
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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

        val timeOptions = options.toMutableMap()
        timeOptions["yAxis"] = "time"
        val timeConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), timeOptions)
        assertEquals(operation.getYAxisByOption(timeConfig), 1.0)

        val flopsOptions = options.toMutableMap()
        flopsOptions["yAxis"] = "flops"
        val flopsConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), flopsOptions)
        assertEquals(operation.getYAxisByOption(flopsConfig), 2.0)

        val bandwidthOptions = options.toMutableMap()
        bandwidthOptions["yAxis"] = "bandwidth"
        val bandwidthConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), bandwidthOptions)
        assertEquals(operation.getYAxisByOption(bandwidthConfig), 3.0)
    }

    @Test
    fun getXAxisByOption() {
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

        val nOptions = options.toMutableMap()
        nOptions["xAxis"] = "n"
        val nConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), nOptions)
        assertEquals(datapoint.getXAxisByConfig(nConfig), 1)

        val rOptions = options.toMutableMap()
        rOptions["xAxis"] = "r"
        val rConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), rOptions)
        assertEquals(datapoint.getXAxisByConfig(rConfig), 2)

        val mOptions = options.toMutableMap()
        mOptions["xAxis"] = "m"
        val mConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), mOptions)
        assertEquals(datapoint.getXAxisByConfig(mConfig), 3)

        val kOptions = options.toMutableMap()
        kOptions["xAxis"] = "k"
        val kConfig = PlotConfiguration(SingleBlasPlot().getPlotDescription(listOf(BLAS_RESULT)), kOptions)
        assertEquals(datapoint.getXAxisByConfig(kConfig), 4)
    }

    @Test
    fun `filter blas datapoints`() {
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
