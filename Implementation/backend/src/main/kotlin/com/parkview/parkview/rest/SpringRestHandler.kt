package com.parkview.parkview.rest

import org.springframework.web.bind.annotation.*

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler(
    private val restHandler: RestHandler,
) : RestHandler {
    @PostMapping("/post")
    override fun postBenchmarkResults(
        @RequestParam sha: String,
        @RequestParam device: String,
        @RequestParam(defaultValue = "false") blas: Boolean,
        @RequestBody json: String,
    ) = restHandler.postBenchmarkResults(sha, device, blas, json)

    @GetMapping("/history")
    override fun getHistory(
        @RequestParam branch: String,
        @RequestParam page: Int,
        @RequestParam benchmark: String,
    ): String = restHandler.getHistory(branch, page, benchmark)


    @GetMapping("/plot")
    override fun getPlot(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
        @RequestParam plotType: String,
        @RequestParam allParams: Map<String, String>,
    ): String = restHandler.getPlot(benchmark, shas, devices, plotType, allParams)

    @GetMapping("/branches")
    override fun getAvailableBranches(): String = restHandler.getAvailableBranches()

    @GetMapping("/benchmarks")
    override fun getAvailableBenchmarks(): String = restHandler.getAvailableBenchmarks()

    @GetMapping("/availablePlots")
    override fun getAvailablePlots(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
    ): String = restHandler.getAvailablePlots(benchmark, shas, devices)

    @GetMapping("/summaryValues")
    override fun getSummaryValue(
        @RequestParam benchmark: String,
        @RequestParam sha: String,
        @RequestParam device: String,
    ): String = restHandler.getSummaryValue(benchmark, sha, device)

    @GetMapping("/averagePerformance")
    override fun getAveragePerformance(
        @RequestParam branch: String,
        @RequestParam benchmark: String,
    ): String = restHandler.getAveragePerformance(branch, benchmark)

    @GetMapping("/numberPages")
    override fun getNumberOfPages(
        @RequestParam branch: String,
    ): String = restHandler.getNumberOfPages(branch)
}