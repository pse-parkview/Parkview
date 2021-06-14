package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit

class BenchmarkResultTypeA: BenchmarkResult {
    override val commit: Commit
        get() = TODO("Not yet implemented")
    override val device: String
        get() = TODO("Not yet implemented")
    override val benchmark: String
        get() = TODO("Not yet implemented")

    override fun toJson(): String {
        TODO("Not yet implemented")
    }

    override fun getSummaryValue(): Double {
        TODO("Not yet implemented")
    }
}