package com.parkview.parkview.processing

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Types of plots available to the frontend
 */
class PlotType private constructor(val name: String) {
    companion object {
        val Line = PlotType("line")
        val Scatter = PlotType("scatter")
        val Bar = PlotType("bar")
    }

    @JsonValue
    override fun toString(): String = name
}
