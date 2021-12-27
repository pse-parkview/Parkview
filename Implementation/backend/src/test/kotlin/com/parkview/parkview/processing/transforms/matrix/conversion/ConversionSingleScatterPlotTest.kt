package com.parkview.parkview.processing.transforms.matrix.conversion

import CONVERSION_RESULT
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PointDataset
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConversionSingleScatterPlotTest {
    private val plot = ConversionSingleScatterPlot()

    @Test
    fun transformConversion() {
        val benchmark = CONVERSION_RESULT

        val options = mutableMapOf(
            "xAxis" to "rows",
            "yAxis" to "time",
            "minRows" to "1",
            "maxRows" to "10",
            "minColumns" to "2",
            "maxColumns" to "20",
            "minNonzeros" to "3",
            "maxNonzeros" to "30",
        )

        val config = PlotConfiguration(plot.getPlotDescription(listOf(benchmark)), options)

        val data = plot.transformConversion(listOf(benchmark), config)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
