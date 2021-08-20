package com.parkview.parkview.processing.transforms

import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.NumericalOption
import com.parkview.parkview.processing.PlotDescription
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class PlotConfigurationTest {
    private val categoricalOption = CategoricalOption(name = "cat", options = listOf("a", "b"))
    private val numericalOption = NumericalOption(name = "num", default = 1.0)

    private val testDescription = PlotDescription(
        name = "test",
        plottableAs = emptyList(),
        options = listOf(categoricalOption, numericalOption)
    )

    private val values = mapOf(
        "num" to "7",
        "cat" to "b",
    )

    private val plotConfiguration = PlotConfiguration(testDescription, values)

    @Test
    fun `test retrieving options`() {
        assertEquals(plotConfiguration.getCategoricalOption(categoricalOption), "b")
        assertEquals(plotConfiguration.getNumericalOption(numericalOption), 7.0, 0.0001)
    }

    @Test
    fun `tests invalid option`() {
        assertThrows<InvalidPlotConfigNameException> {
            plotConfiguration.getCategoricalOption(numericalOption)
        }
        assertThrows<InvalidPlotConfigNameException> {
            plotConfiguration.getNumericalOption(categoricalOption)
        }
    }

    @Test
    fun `tests invalid plot description`() {
        assertThrows<InvalidPlotConfigValueException> {
            PlotConfiguration(testDescription, mapOf("num" to "7"))
        }
        assertThrows<InvalidPlotConfigValueException> {
            PlotConfiguration(testDescription, mapOf("cat" to "b"))
        }
        assertThrows<InvalidPlotConfigValueException> {
            PlotConfiguration(testDescription, mapOf("cat" to "c"))
        }
        assertThrows<InvalidPlotConfigValueException> {
            PlotConfiguration(testDescription, mapOf("num" to "c"))
        }
    }
}
