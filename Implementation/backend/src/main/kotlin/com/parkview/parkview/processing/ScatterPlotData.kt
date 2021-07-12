package com.parkview.parkview.processing

import com.google.gson.Gson

data class ScatterPoint(
    val x: Double,
    val y: Double,
)

data class ScatterSeries(
    val data: MutableList<ScatterPoint>,
    val label: String,
)

/**
 * data type for chart.js scatter plots
 */
class ScatterPlotData(
    private val series: List<ScatterSeries>,
) : PlottableData {


    override fun toJson(): String = Gson().toJson(series)
}