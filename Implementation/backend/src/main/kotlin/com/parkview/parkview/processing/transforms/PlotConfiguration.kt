package com.parkview.parkview.processing.transforms

import com.parkview.parkview.processing.PlotDescription
import com.parkview.parkview.processing.PlotOption

/**
 * Class that models a configuration for a plot.
 *
 * @param description plot description this configuration is based on
 * @param values the values this configuration should contain
 */
class PlotConfiguration(
    description: PlotDescription,
    values: Map<String, String>,
) {
    init {
        for ((key, value) in values) {
            val option = description.options.find { it.name == key } ?: continue
            if ((value !in option.options) and !option.number) throw InvalidPlotConfigValueException(values[key], key)
        }
    }

    private val categoricalOptions: Map<String, String> =
        description.options.filter { !it.number }.map { it.name }
            .associateWith { (values[it] ?: throw InvalidPlotConfigValueException(values[it], it)) }

    private val numericalOptions: Map<String, Double> =
        description.options.filter { it.number }.map { it.name }.associateWith {
            (
                values[it]?.toDoubleOrNull() ?: throw InvalidPlotConfigValueException(
                    values[it],
                    it,
                )
                )
        }

    /**
     * Returns the value for a categorical option.
     *
     * @param option the given option
     * @return the configured value for the given option
     *
     * @throws InvalidPlotConfigNameException if the option does not exist in this configuration
     */
    fun getCategoricalOption(option: PlotOption) =
        categoricalOptions[option.name] ?: throw InvalidPlotConfigNameException(option.name)

    /**
     * Returns the value for a categorical option.
     *
     * @param option the given option
     * @return the configured value for the given option
     *
     * @throws InvalidPlotConfigNameException if the option does not exist in this configuration
     */
    fun getCategoricalOption(option: String) =
        categoricalOptions[option] ?: throw InvalidPlotConfigNameException(option)

    /**
     * Returns the value for a categorical option.
     *
     * @param option the given option name
     * @return the configured value for the given option
     *
     * @throws InvalidPlotConfigNameException if the option does not exist in this configuration
     */
    fun getNumericalOption(option: PlotOption) =
        numericalOptions[option.name] ?: throw InvalidPlotConfigNameException(option.name)

    /**
     * Returns the value for a categorical option.
     *
     * @param option the given option name
     * @return the configured value for the given option
     *
     * @throws InvalidPlotConfigNameException if the option does not exist in this configuration
     */
    fun getNumericalOption(option: String) =
        numericalOptions[option] ?: throw InvalidPlotConfigNameException(option)
}
