package com.parkview.parkview.git

/**
 * Null object for representing a an empty or non existing benchmark result
 */
class EmptyBenchmarkResult: BenchmarkResult {
    override fun toJson(): String {
        TODO("Not yet implemented")
    }

    override fun getSummaryValue(): Double {
        TODO("Not yet implemented")
    }
}