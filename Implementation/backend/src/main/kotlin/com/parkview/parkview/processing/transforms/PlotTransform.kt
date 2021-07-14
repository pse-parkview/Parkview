package com.parkview.parkview.processing.transforms

import com.parkview.parkview.processing.PlotType

interface PlotTransform {
    val numAllowedInputs: Pair<Int, Int>
    val plottableAs: List<PlotType>
    val name: String
}