package com.parkview.parkview.database

import com.parkview.parkview.git.*

class TypeCachingDatabaseHandler(
    private val handler: DatabaseHandler
) : DatabaseHandler {


    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        handler.updateBenchmarkResults(results)
    }

    override fun fetchBenchmarkResult(
        commit: Commit,
        device: Device,
        benchmark: Benchmark,
        rowLim: Long,
        colLim: Long,
        nonzerosLim: Long
    ): BenchmarkResult = handler.fetchBenchmarkResult(commit, device, benchmark, rowLim, colLim, nonzerosLim)

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: Benchmark): Boolean =
        handler.hasDataAvailable(commit, device, benchmark)

    override fun getAvailableDevices(commit: Commit, benchmark: Benchmark): List<Device> =
        handler.getAvailableDevices(commit, benchmark)

    override fun getAvailableBenchmarks(): List<Benchmark> = handler.getAvailableBenchmarks()

    override fun getBenchmarkTypeForName(benchmarkName: String): BenchmarkType =
        handler.getBenchmarkTypeForName(benchmarkName)
}