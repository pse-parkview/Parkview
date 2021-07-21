package com.parkview.parkview.processing.transforms

import com.google.gson.Gson

data class PlotPoint(
    val x: Double,
    val y: Double,
)

data class PointDataset(
    val data: List<PlotPoint>,
    val label: String,
) : Dataset

interface Dataset

/**
 * data type for chart.js line and scatter plots
 */
class DatasetSeries(
    private val datasets: List<Dataset>,
    private val labels: List<String> = emptyList(),
) : PlottableData {
    override fun toJson(): String = Gson().toJson(this)
}