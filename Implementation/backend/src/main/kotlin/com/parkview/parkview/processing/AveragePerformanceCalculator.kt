package com.parkview.parkview.processing

import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset

class AveragePerformanceCalculator(
    private val databaseHandler: DatabaseHandler,
) {
    fun getAveragePerformanceData(commits: List<Commit>, benchmark: BenchmarkType, device: Device): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        // reversed because we want the earliest commit on the left
        for (commit in commits.reversed()) {
            if (databaseHandler.hasDataAvailable(commit, device, benchmark)) {
                val benchmarkResult = databaseHandler.fetchBenchmarkResult(commit, device, benchmark)
                for ((name, summaryValue) in benchmarkResult.summaryValues) {
                    seriesByName.getOrPut(name) { mutableListOf() } += PlotPoint(
                        x = commit.date.time.toDouble(),
                        y = summaryValue,
                    )
                }
            }
        }

        return PlottableData(
            seriesByName.map { (key, value) ->
                PointDataset(
                    label = key,
                    data = value.sortedBy { it.x }.toMutableList(),
                )
            },
        )
    }
}
