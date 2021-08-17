package com.parkview.parkview.tracking

import com.parkview.parkview.git.BenchmarkResult

interface Webhook {
    fun notify(new: BenchmarkResult, previous: BenchmarkResult)
}
