package com.parkview.parkview.processing

/**
 * Interface for data that is the result of a plot transform.
 */
interface PlottableData {
    /**
     * Serializes the data using the JSON format.
     *
     * @return string containing the data in json format.
     */
    fun toJson(): String
}