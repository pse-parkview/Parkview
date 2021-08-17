package com.parkview.parkview.tracking

import com.parkview.parkview.git.BenchmarkResult

class SimpleTerminalHook : Webhook {
    override fun notify(new: BenchmarkResult, previous: BenchmarkResult) {
        for ((algorithm, value) in new.summaryValues) {
            if (!previous.summaryValues.containsKey(algorithm)) continue

            println("#### $algorithm ####")

            println("NEW RESULT SUMMARY: $value")
            println("PREVIOUS RESULT SUMMARY: ${previous.summaryValues[algorithm]}")

            val difference = value - previous.summaryValues[algorithm]!!

            if (difference == 0.0) {
                println("STAYED THE SAME")
                continue
            }

            val trendString = if (difference < 0) {
                "WORSENED BY"
            } else {
                "IMPROVED BY"
            }

            println("$trendString $difference")
        }
    }
}