package com.parkview.parkview.rest

import com.google.gson.Gson
import com.parkview.parkview.AppConfig
import com.parkview.parkview.benchmark.JsonParser
import com.parkview.parkview.database.AnnotatingRepositoryHandler
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.exposed.ExposedJsonHandler
import com.parkview.parkview.git.*
import com.parkview.parkview.processing.AvailablePlots
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.util.*

/**
 * Class that implements a RestHandler using the Spring framework
 */
@RestController
class SpringRestHandler(
    private val appConfig: AppConfig
) : RestHandler {
    private val databaseHandler: DatabaseHandler = ExposedJsonHandler(
        HikariDataSource(HikariConfig()
            .apply {
                jdbcUrl = appConfig.datasource.jdbcUrl
                username = appConfig.datasource.username
                password = appConfig.datasource.password
            }
        )
    )

    private val repHandler = AnnotatingRepositoryHandler(
        CachingRepositoryHandler(
            GitApiHandler(
                appConfig.gitApi.repoName,
                appConfig.gitApi.owner,
                appConfig.gitApi.username,
                appConfig.gitApi.token,
            ),
            maxCached = appConfig.gitApi.maxCached,
            branchLifetime = appConfig.gitApi.branchLifetime,
            branchListLifetime = appConfig.gitApi.branchListLifetime,
        ),
        databaseHandler
    )


    @PostMapping("/post")
    override fun handlePost(
        @RequestParam sha: String,
        @RequestParam device: String,
        @RequestParam(defaultValue = "false") blas: Boolean,
        @RequestBody json: String,
    ) {
        val benchmarkResults = JsonParser.benchmarkResultsFromJson(sha, device, json, blas = blas)

        databaseHandler.insertBenchmarkResults(benchmarkResults)
    }

    @GetMapping("/history")
    override fun handleGetHistory(
        @RequestParam branch: String,
        @RequestParam page: Int,
        @RequestParam benchmark: String,
    ): String {
        val commits = repHandler.fetchGitHistory(branch, page, BenchmarkType.valueOf(benchmark))
        return Gson().toJson(commits)
    }


    @GetMapping("/plot")
    override fun handleGetPlotData(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
        @RequestParam plotType: String,
        @RequestParam allParams: Map<String, String>,
    ): String {
        val plotParams = allParams.toMutableMap().apply {
            remove("benchmark")
            remove("devices")
            remove("shas")
            remove("plotType")
        }
        val results: List<BenchmarkResult> = shas.zip(devices).map { (sha, device) ->
            databaseHandler.fetchBenchmarkResult(
                Commit(sha, "", Date(), ""),
                Device(device),
                BenchmarkType.valueOf(benchmark),
            )
        }

        val plot = AvailablePlots.getPlotByName(plotType) ?: throw IllegalArgumentException("Invalid plot type")
        return plot.transform(results, plotParams).toJson()
    }

    @GetMapping("/branches")
    override fun getAvailableBranches(): String {
        val branches = repHandler.getAvailableBranches()
        val gson = Gson()
        return gson.toJson(branches)
    }

    @GetMapping("/benchmarks")
    override fun getAvailableBenchmarks(): String {
        val benchmarks = BenchmarkType.values()
        return Gson().toJson(benchmarks)
    }

    @GetMapping("/availablePlots")
    override fun getAvailablePlots(
        @RequestParam benchmark: String,
        @RequestParam shas: List<String>,
        @RequestParam devices: List<String>,
    ): String = Gson().toJson(
        AvailablePlots.getPlotList(
            BenchmarkType.valueOf(benchmark),
            shas.size,
        )
    )
}