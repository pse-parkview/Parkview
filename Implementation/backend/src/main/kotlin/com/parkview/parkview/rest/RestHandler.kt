package com.parkview.parkview.rest

import org.springframework.web.bind.annotation.RequestParam

/**
 * Interface that provides methods for handling POST and GET requests
 */
interface RestHandler {
    /**
     * Handles a POST request for uploading benchmark results
     *
     * @param json request body as json
     */
    fun handlePost(json: String)

    /**
     * Handles a GET request for retrieving commit history
     *
     * @param json request body as json
     */
    fun handleGetHistory(branch: String, page: Int): String

    /**
     * Handles a GET request for retrieving benchmark results
     *
     * @param json request body as json
     */
    fun handleGetBenchmarkResults(@RequestParam benchmark: String, @RequestParam sha: String, @RequestParam device: String): String

    fun getAvailableBranches(): String
}