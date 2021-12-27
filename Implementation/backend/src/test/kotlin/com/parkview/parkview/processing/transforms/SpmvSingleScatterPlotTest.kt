package com.parkview.parkview.processing.transforms

import SPMV_RESULT
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSingleScatterPlot
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class SpmvSingleScatterPlotTest {
    private val transform = SpmvSingleScatterPlot()

    @Test
    fun test_exception_on_calling_with_invalid_number_inputs() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotConfigValueException> {
            PlotConfiguration(transform.getPlotDescription(results), mapOf("xAxis" to "nonzeros"))
        }
    }

    @Test
    fun test_exception_on_calling_with_invalid_xAxis() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotConfigValueException> {
            PlotConfiguration(transform.getPlotDescription(results), mapOf("eine kleine ente" to "schwimmt im see"))
        }
    }
}
