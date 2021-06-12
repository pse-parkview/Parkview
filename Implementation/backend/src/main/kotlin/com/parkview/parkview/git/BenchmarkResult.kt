package com.parkview.parkview.git

abstract class BenchmarkResult {
    abstract fun getData(): Any // TODO: how do return that stuff?
    abstract fun toJson(): String
    abstract fun getSummaryValue(): Double
}