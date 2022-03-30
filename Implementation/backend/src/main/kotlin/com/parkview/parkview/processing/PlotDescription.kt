package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult

/**
 * Full description of a plot.
 *
 * @param plotName name of plot
 * @param plottableAs list of available [PlotTypes][PlotType] for this plot
 * @param options list of [PlotOptions][PlotOption] for this plot
 */
data class PlotDescription(
    val plotName: String,
    val plottableAs: Array<PlotType>,
    val options: Array<PlotOption>,
)

/**
 * Models a single PlotOption
 *
 * @param name name of option
 * @param options available categorical values for this option
 * @param default default value
 * @param number if true this option gets interpreted as an option containing a number
 * @param description describes the option
 */
abstract class PlotOption(
    val name: String,
    val options: Array<String> = emptyArray(),
    val default: String = options.first(),
    val number: Boolean = options.isEmpty(),
    val description: String = "",
) {
    init {
        if ((!number) and (default !in options))
            throw IllegalArgumentException("Default value has to be available option")
    }
}

/**
 * Option that depends on the inputs for the plot
 *
 * @param name name of option
 * @param description describes the option
 */
abstract class DynamicOption(
    name: String,
    description: String = "",
) : PlotOption(name = name, options = emptyArray(), default = "", description = description) {
    /**
     * Fills in missing values for the option from the inputted benchmark result
     *
     * @param results list of benchmark results
     *
     * @return concrete option for plot
     */
    abstract fun realizeOption(results: Array<BenchmarkResult>): PlotOption
}

/**
 * Categorical option that depends on the inputs for the plot
 *
 * @param name name of option
 * @param description describes the option
 */
abstract class DynamicCategoricalOption(
    name: String,
    description: String = ""
) : DynamicOption(name, description) {
    final override fun realizeOption(results: Array<BenchmarkResult>): PlotOption = CategoricalOption(
        name,
        getOptions(results),
        getDefault(results),
        description,
    )

    /**
     * Methods that gets overridden by subclasses. Returns the available options.
     *
     * @param results list of results
     *
     * @return list of options
     */
    abstract fun getOptions(results: Array<BenchmarkResult>): Array<String>

    /**
     * Returns the default value for this option.
     *
     * @param results list of results
     *
     * @return default value of option
     */
    open fun getDefault(results: Array<BenchmarkResult>): String = getOptions(results).first()
}

/**
 * Numerical option that depends on the inputs for the plot
 *
 * @param name name of option
 * @param description describes the option
 */
abstract class DynamicNumericalOption(
    name: String,
    description: String = ""
) : DynamicOption(name, description) {
    final override fun realizeOption(results: Array<BenchmarkResult>): PlotOption = NumericalOption(
        name,
        getDefault(results),
        description,
    )

    /**
     * Methods that gets overridden by subclasses. Returns the default value.
     *
     * @param results list of results
     *
     * @return list of options
     */
    abstract fun getDefault(results: Array<BenchmarkResult>): Double
}

/**
 * [PlotOption] that can only take discrete values.
 *
 * @param name name of option
 * @param options available categorical values for this option
 * @param default default value
 * @param description describes the option
 */
class CategoricalOption(
    name: String,
    options: Array<String>,
    default: String = options.first(),
    description: String = "",
) : PlotOption(name = name, options = options, default = default, description = description)

/**
 * [PlotOption] that can take continuous number values.
 *
 * @param name name of option.
 * @param default default value
 * @param description description of option
 */
class NumericalOption(name: String, default: Double = 0.0, description: String = "") :
    PlotOption(name = name, default = default.toString(), description = description)
