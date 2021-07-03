package com.parkview.parkview.git

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import java.util.*

internal class BranchForBenchmarkTest {
    private lateinit var branch: BranchForBenchmark
    private val a = Commit("a", "", Date(), "")
    private val b = Commit("b", "", Date(), "")
    private val c = Commit("c", "", Date(), "")

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