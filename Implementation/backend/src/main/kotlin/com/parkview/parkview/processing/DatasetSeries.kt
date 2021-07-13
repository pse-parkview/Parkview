package com.parkview.parkview.processing

import com.google.gson.Gson

data class PlotPoint(
    val x: Double,
    val y: Double,
)

data class Dataset(
    val data: MutableList<PlotPoint>,
    val label: String,
)

/**
 * data type for chart.js scatter plots
 */
class DatasetSeries(
    private val series: List<Dataset>,
) : PlottableData {
    override fun toJson(): String = Gson().toJson(series)
}