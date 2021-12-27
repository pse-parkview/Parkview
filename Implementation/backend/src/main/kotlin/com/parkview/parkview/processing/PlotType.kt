package com.parkview.parkview.processing


/**
 * Types of plots available to the frontend
 */
class PlotType private constructor(val name: String) {
    companion object {
        val Line = PlotType("line")
        val Scatter = PlotType("scatter")
        val Bar = PlotType("bar")
    }

    override fun toString(): String = name
}
