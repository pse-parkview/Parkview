package com.parkview.parkview.processing

import com.google.gson.Gson
import com.google.gson.GsonBuilder

data class LabeledPoint(
    val x: Double,
    val y: Double,
    val label: String,
)

class PointList(
    private val points: List<LabeledPoint>,
) : PlottableData {
    private val gson = Gson()

    override fun toJson(): String = gson.toJson(points)
}