package com.parkview.parkview.git

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import java.util.*

internal class BranchForBenchmarkTest {
    private lateinit var branch: BranchForBenchmark
    private val a = Commit("a", "", Date(), null)
    private val b = Commit("b", "", Date(), a)
    private val c = Commit("c", "", Date(), b)

    @BeforeEach
    fun setup() {
        val commits = listOf(a, b, c)
        branch = BranchForBenchmark("schmock", Benchmark("schmack", BenchmarkType.BlasBenchmark), commits)
    }

    @Test
    fun `Test getCommit() for a generic commit`() {
        val result = branch.getCommit("a")
        assert(result == a)
    }

    @Test
    fun `Test getCommit() for a not existing commit`() {
        val result = branch.getCommit("d")
        assert(result == null)
    }
}