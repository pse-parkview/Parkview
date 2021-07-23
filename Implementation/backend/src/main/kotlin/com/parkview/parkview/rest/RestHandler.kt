package com.parkview.parkview.rest

import com.parkview.parkview.git.Commit
import com.parkview.parkview.processing.PlotList
import com.parkview.parkview.processing.transforms.PlottableData
import org.springframework.web.bind.annotation.RequestParam

/**
 * Interface that provides methods for handling POST and GET requests
 */
interface RestHandler {
    /**
     * Handles a POST request for uploading benchmark results. It should be noted that if a given datapoint
     * already exists (that means same problem setup), it gets updated with the new values for the algorithms.
     *
     * @param sha commit this benchmark has been run on
     * @param device device this benchmark has been run on
     * @param blas true if benchmark result is in blas format
     * @param json request body as json
     */
    fun postBenchmarkResults(sha: String, device: String, blas: Boolean, json: String)

    /**
     * Handles a GET request for retrieving commit history. Returns 30 commits per page.
     *
     * @param branch branch name
     * @param page page number
     * @param benchmark benchmark type
     *
     * @return history in JSON format
     */
    fun getHistory(branch: String, page: Int, benchmark: String): List<Commit>

    /**
     * Handles a GET request for retrieving plot data for the given benchmark results and plot setup.
     * The data gets returned in a format usable by Chart.js without any further processing.
     *
     * @param benchmark benchmark type
     * @param shas list of commit shas for benchmarks
     * @param devices list of devices for benchmarks
     * @param plotType what type of transform should be applied
     * @param plotParams additional options for the plot
     */
    fun getPlot(
        benchmark: String,
        shas: List<String>,
        devices: List<String>,
        plotType: String,
        plotParams: Map<String, String>,
    ): PlottableData

    /**
     * Returns a list of available branches
     *
     * @return list of branches
     */
    fun getAvailableBranches(): List<String>

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
        @RequestParam devices: List<String>,
    ): PlotList

    /**
     * Returns a list of all available benchmarks
     *
     * @return list of available benchmarks
     */
    fun getAvailableBenchmarks(): List<String>


    fun getSummaryValue(benchmark: String, sha: String, device: String): Map<String, Double>

    /**
     * Returns line chart data for the average performance score in a Chart.js format.
     *
     * @param branch name of branch
     * @param benchmark name of benchmark
     *
     * @return plottable data for line charts
     */
    fun getAveragePerformance(branch: String, benchmark: String): PlottableData

    /**
     * Returns the number of pages as an Int.
     */
    fun getNumberOfPages(branch: String): Int
}