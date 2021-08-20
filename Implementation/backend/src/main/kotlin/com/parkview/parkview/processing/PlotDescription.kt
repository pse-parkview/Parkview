package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult

data class PlotDescription(
    val plotName: String,
    val plottableAs: List<PlotType>,
    val options: List<PlotOption>,
)

abstract class PlotOption(
    val name: String,
    val options: List<String> = emptyList(),
    val default: String = options.first(),
    val number: Boolean = options.isEmpty(),
    val description: String = "",
) {
    init {
        if ((!number) and (default !in options))
            throw IllegalArgumentException("Default value has to be available option")
    }
}

abstract class DynamicOption(
    name: String,
    description: String = "",
) : PlotOption(name = name, options = emptyList(), default = "", description = description) {
    abstract fun realizeOption(results: List<BenchmarkResult>): PlotOption
}

abstract class DynamicCategoricalOption(
    name: String,
    description: String = ""
) : DynamicOption(name, description) {
    final override fun realizeOption(results: List<BenchmarkResult>): PlotOption = CategoricalOption(
        name,
        getOptions(results),
        getDefault(results),
        description,
    )

    abstract fun getOptions(results: List<BenchmarkResult>): List<String>

    open fun getDefault(results: List<BenchmarkResult>): String = getOptions(results).first()
}

abstract class DynamicNumericalOption(
    name: String,
    description: String = ""
) : DynamicOption(name, description) {
    final override fun realizeOption(results: List<BenchmarkResult>): PlotOption = NumericalOption(
        name,
        getDefault(results),
        description,
    )

    abstract fun getDefault(results: List<BenchmarkResult>): Double
}

class CategoricalOption(
    name: String,
    options: List<String>,
    default: String = options.first(),
    description: String = "",
) : PlotOption(name = name, options = options, default = default, description = description)

class NumericalOption(name: String, default: Double = 0.0, description: String = "") :
    PlotOption(name = name, default = default.toString(), description = description)