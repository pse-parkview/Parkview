package com.parkview.parkview.processing

import com.google.gson.Gson

data class LinePlotSeries(
    val label: String,
    val data: List<Double>,
)

data class LinePlotData(
    private val labels: List<Long>,
    private val datasets: List<LinePlotSeries>,
) : PlottableData {
    override fun toJson(): String = Gson().toJson(this)
}