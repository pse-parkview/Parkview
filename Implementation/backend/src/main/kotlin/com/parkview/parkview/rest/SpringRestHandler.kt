package com.parkview.parkview.rest

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.parkview.parkview.processing.PlotDescription
import com.parkview.parkview.processing.transforms.PlottableData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
        @RequestParam md5: String,
    ) = restHandler.postBenchmarkResults(sha, device, json,md5)

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
        @RequestParam plotParams: Map<String, String>,
    ): PlottableData = restHandler.getPlot(
        benchmark, shas, devices, plotType,
        plotParams.toMutableMap().apply {
            remove("benchmark")
            remove("shas")
            remove("devices")
            remove("plotType")
        }
    )

    @GetMapping("/branches")
    override fun getAvailableBranches(): List<String> = restHandler.getAvailableBranches()

    @GetMapping("/benchmarks")
    override fun getAvailableBenchmarks(): List<String> = restHandler.getAvailableBenchmarks()

    @GetMapping("/availablePlots")
    override fun getAvailablePlots(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
    ): List<PlotDescription> = restHandler.getAvailablePlots(benchmark, shas, devices)

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
        device: String,
    ): PlottableData = restHandler.getAveragePerformance(branch, benchmark, device)

    @GetMapping("/numberPages")
    override fun getNumberOfPages(
        @RequestParam branch: String,
    ): Int = restHandler.getNumberOfPages(branch)

    @GetMapping("/availableDevices")
    override fun getAvailableDevices(branch: String, benchmark: BenchmarkType): List<Device> =
        restHandler.getAvailableDevices(branch, benchmark)
}
