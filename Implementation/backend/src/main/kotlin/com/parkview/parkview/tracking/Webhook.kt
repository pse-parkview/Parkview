package com.parkview.parkview.tracking

import com.parkview.parkview.git.BenchmarkResult

interface Webhook {
    fun addResult(new: BenchmarkResult, previous: BenchmarkResult)

    fun notifyHook()
}
