package com.parkview.parkview.processing.transforms

import SPMV_RESULT
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class SpmvSingleScatterPlotTest {
    private val transform = SpmvSingleScatterPlot(SpmvSingleScatterPlotYAxis.Bandwidth)

    @Test
    fun `test exception on calling with invalid number inputs`() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotTransformException> {
            transform.transform(results, "nonzeros")
        }
    }

    @Test
    fun `test exception on calling with invalid xAxis`() {
        val results = listOf(SPMV_RESULT, SPMV_RESULT)

        assertFailsWith<InvalidPlotTransformException> {
            transform.transform(results, "eine kleine ente")
        }
    }
}