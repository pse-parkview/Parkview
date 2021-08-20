package com.parkview.parkview.processing.transforms

/**
 * Interface for datasets plottable by Chart.js
 */
interface Dataset {
    val label: String
}

/**
 * Dataset used for bar charts
 *
 * @param label label for dataset
 * @param data data for dataset
 */
data class BarChartDataset(
    override val label: String,
    val data: List<Double>,
) : Dataset

/**
 * Dataset used for scatter and line plots
 *
 * @param label label for dataset
 * @param data data for dataset
 */
data class PointDataset(
    override val label: String,
    val data: List<PlotPoint>,
) : Dataset

/**
 * Single plot point
 *
 * @param x x value
 * @param y y value
 */
data class PlotPoint(
    val x: Double,
    val y: Double,
)

/**
 * Data type for chart.js
 *
 * @param datasets datasets for plot
 * @param datasets labels for plot
 */
class PlottableData(
    val datasets: List<Dataset>,
    val labels: List<String> = emptyList(),
)
