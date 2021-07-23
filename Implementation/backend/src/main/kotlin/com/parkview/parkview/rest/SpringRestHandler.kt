package com.parkview.parkview.rest

import com.parkview.parkview.git.Commit
import com.parkview.parkview.processing.PlotList
import com.parkview.parkview.processing.transforms.PlottableData
import org.springframework.web.bind.annotation.*

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
@RequestMapping("/parkview")
class SpringRestHandler(
    private val restHandler: RestHandler,
) : RestHandler {
    @PostMapping("/post")
    override fun postBenchmarkResults(
        @RequestParam sha: String,
        @RequestParam device: String,
        @RequestBody json: String,
    ) = restHandler.postBenchmarkResults(sha, device, json)

    @GetMapping("/history")
    override fun getHistory(
        @RequestParam branch: String,
        @RequestParam page: Int,
        @RequestParam benchmark: String,
    ): List<Commit> = restHandler.getHistory(branch, page, benchmark)


    @GetMapping("/plot")
    override fun getPlot(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
        @RequestParam plotType: String,
        @RequestParam allParams: Map<String, String>,
    ): PlottableData = restHandler.getPlot(benchmark, shas, devices, plotType, allParams)

    @GetMapping("/branches")
    override fun getAvailableBranches(): List<String> = restHandler.getAvailableBranches()

    @GetMapping("/benchmarks")
    override fun getAvailableBenchmarks(): List<String> = restHandler.getAvailableBenchmarks()

    @GetMapping("/availablePlots")
    override fun getAvailablePlots(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
    ): PlotList = restHandler.getAvailablePlots(benchmark, shas, devices)

    @GetMapping("/summaryValues")
    override fun getSummaryValue(
        @RequestParam benchmark: String,
        @RequestParam sha: String,
        @RequestParam device: String,
    ): Map<String, Double> = restHandler.getSummaryValue(benchmark, sha, device)

    @GetMapping("/averagePerformance")
    override fun getAveragePerformance(
        @RequestParam branch: String,
        @RequestParam benchmark: String,
    ): PlottableData = restHandler.getAveragePerformance(branch, benchmark)

    @GetMapping("/numberPages")
    override fun getNumberOfPages(
        @RequestParam branch: String,
    ): Int = restHandler.getNumberOfPages(branch)
}