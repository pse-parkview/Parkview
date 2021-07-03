package com.parkview.parkview.processing

import com.google.gson.Gson

data class LabeledPoint(
    val x: Double,
    val y: Double,
    val label: String,
)

/**
 * Contains a list of [LabeledPoint]. The list has no order.
 */
class PointList(
    private val points: List<LabeledPoint>,
) : PlottableData {
    private val gson = Gson()

    override fun toJson(): String = gson.toJson(points)
}