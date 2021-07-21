package com.parkview.parkview.processing

import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.processing.transforms.*

class AveragePerformanceCalculator(
    private val databaseHandler: DatabaseHandler,
) {
    fun getAveragePerformanceData(commits: List<Commit>, benchmark: BenchmarkType): PlottableData {
        val averageValuesByCommit: MutableMap<Commit, Map<String, Double>> = mutableMapOf()

        for (commit in commits) {
            val devices = databaseHandler.getAvailableDevices(commit, benchmark)
            var averageSummaryValues: Map<String, Double> = emptyMap()
            for (device in devices) {
                val benchmarkResult = databaseHandler.fetchBenchmarkResult(commit, device, benchmark)
                averageSummaryValues = (benchmarkResult.summaryValues.asSequence() + averageSummaryValues.asSequence())
                    .distinct()
                    .groupBy({ it.key }, { it.value })
                    .mapValues { (_, values) -> values.sum() / devices.size }
            }

            averageValuesByCommit[commit] = averageSummaryValues
        }

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for ((commit, summaryValues) in averageValuesByCommit) {
            for ((name, summaryValue) in summaryValues) {
                seriesByName.getOrPut(name) { mutableListOf() } += PlotPoint(
                    x = commit.date.time.toDouble(), // TODO: fix this, dont send it in this format
                    y = summaryValue,
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) ->
            PointDataset(
                label = key,
                data = value.sortedBy { it.x }.toMutableList(),
            )
        })
    }
}