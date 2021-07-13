package com.parkview.parkview.rest

/**
 * Interface that provides methods for handling POST and GET requests
 */
interface RestHandler {
    /**
     * Handles a POST request for uploading benchmark results
     *
     * @param json request body as json
     */
    fun handlePost(sha: String, device: String, benchmark: String, blas: Boolean, json: String)

    /**
     * Handles a GET request for retrieving commit history
     *
     * @param json request body as json
     */
    fun handleGetHistory(branch: String, page: Int, benchmark: String): String

    /**
     * Handles a GET request for retrieving benchmark results
     *
     * @param json request body as json
     */
    fun handleGetPlotData(
        benchmark: String,
        sha: List<String>,
        devices: List<String>,
        plotType: String,
    ): String

    fun getAvailableBranches(): String
}