package com.parkview.parkview.processing.transforms

data class BarChartDataset(
    val label: String,
    val data: List<Double>,
) : Dataset

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
    override val datasets: List<Dataset>,
    override val labels: List<String> = emptyList(),
) : PlottableData

/**
 * Interface for data that is the result of a plot transform.
 */
interface PlottableData {

    val labels: List<String>

    val datasets: List<Dataset>
}