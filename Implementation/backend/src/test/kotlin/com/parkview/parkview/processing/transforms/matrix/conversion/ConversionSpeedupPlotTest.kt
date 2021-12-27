package com.parkview.parkview.processing.transforms.matrix.conversion

import CONVERSION_RESULT
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PointDataset
import kotlin.test.assertEquals
import kotlin.test.Test

internal class ConversionSpeedupPlotTest {
    private val plot = ConversionSpeedupPlot()

    private val benchmark = CONVERSION_RESULT

    @Test
    fun transformConversion() {
        val options = mutableMapOf(
            "baseline" to "${benchmark.identifier}",
            "xAxis" to "rows",
            "minRows" to "1",
            "maxRows" to "10",
            "minColumns" to "2",
            "maxColumns" to "20",
            "minNonzeros" to "3",
            "maxNonzeros" to "30",
        )

        val config = PlotConfiguration(plot.getPlotDescription(listOf(benchmark, benchmark)), options)
        val data = plot.transformConversion(listOf(benchmark, benchmark), config)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
