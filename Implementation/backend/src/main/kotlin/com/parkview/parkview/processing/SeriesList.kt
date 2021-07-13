package com.parkview.parkview.processing

import com.google.gson.Gson

data class Series(
    val name: String,
    var series: List<Point>
)

data class Point(
    val name: String,
    val value: Double,
)

/**
 * Contains a list of [Point]. The list has no order.
 */
class SeriesList(
    private val series: List<Series>,
) : PlottableData {
    private val gson = Gson()

    override fun toJson(): String = gson.toJson(series)
}