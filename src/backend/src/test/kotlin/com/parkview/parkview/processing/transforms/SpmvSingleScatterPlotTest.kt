package com.parkview.parkview.processing.transforms

import SPMV_RESULT
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSingleScatterPlot
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class SpmvSingleScatterPlotTest {
    private val transform = SpmvSingleScatterPlot()

    @Test
    fun `test exception on calling with invalid number inputs`() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotConfigValueException> {
            PlotConfiguration(transform.getPlotDescription(results), mapOf("xAxis" to "nonzeros"))
        }
    }

    @Test
    fun `test exception on calling with invalid xAxis`() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotConfigValueException> {
            PlotConfiguration(transform.getPlotDescription(results), mapOf("eine kleine ente" to "schwimmt im see"))
        }
    }
}
