package com.parkview.parkview.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * Interface that provides methods for handling POST and GET requests
 */
interface RestHandler {
    /**
     * Handles a POST request for uploading benchmark results
     *
     * @param sha commit this benchmark has been run on
     * @param device device this benchmark has been run on
     * @param blas true if benchmark result is in blas format
     * @param json request body as json
     */
    fun handlePost(sha: String, device: String, blas: Boolean, json: String)

    /**
     * Handles a GET request for retrieving commit history
     *
     * @param branch branch name
     * @param page page number
     * @param benchmark benchmark type
     *
     * @return history in JSON format
     */
    fun handleGetHistory(branch: String, page: Int, benchmark: String): String

    /**
     * Handles a GET request for retrieving benchmark results
     *
     * @param benchmark benchmark type
     * @param shas list of commit shas for benchmarks
     * @param devices list of devices for benchmarks
     * @param plotType what type of transform should be applied
     * @param plotParams additional options for the plot
     */
    fun handleGetPlotData(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
        plotType: String,
        plotParams: Map<String, String>,
    ): String

    /**
     * Returns a list of available branches
     *
     * @return list of branches
     */
    fun getAvailableBranches(): String

    /**
     * Returns a list of available plot transforms grouped by plot type
     *
     * @param benchmark benchmark name
     * @param shas list of commits
     * @param devices list of devices
     *
     * @return list of available plot transforms grouped by plot type
     */
    fun getAvailablePlots(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>
    ): String

    /**
     * Returns a list of all available benchmarks
     *
     * @return list of available benchmarks
     */
    fun getAvailableBenchmarks(): String


    fun getSummaryValue(benchmark: String, sha: String, device: String): String
}