package com.parkview.parkview.git

import COMMIT_A
import COMMIT_B
import DEVICE
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CachingRepositoryHandlerTest {

    private lateinit var handler: CachingRepositoryHandler

    private val mockHandler = object : RepositoryHandler {
        var pages = 1
        override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> = listOf(
            Commit(
                COMMIT_A.sha,
            ),
        )

        override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> = listOf(
            Commit(
                COMMIT_B.sha,
            )
        )

        override fun getAvailableBranches(): List<String> = listOf("test")

        override fun getNumberOfPages(branch: String): Int = pages++

    }

    @BeforeEach
    fun setUp() {
        mockHandler.pages = 1
        handler = CachingRepositoryHandler(
            mockHandler,
            branchLifetime = Int.MAX_VALUE,
            shaLifetime = Int.MAX_VALUE,
            branchListLifetime = Int.MAX_VALUE,
        )
    }

    @Test
    fun `checks for same object reference for fetch by branch`() {
        val commit1 = handler.fetchGitHistoryByBranch("test", 1, BenchmarkType.Blas)
        val commit2 = handler.fetchGitHistoryByBranch("test", 1, BenchmarkType.Blas)

        assertSame(commit1, commit2)
    }

    @Test
    fun `checks for same object reference for fetch by sha`() {
        val commit1 = handler.fetchGitHistoryBySha(COMMIT_A.sha, 1, BenchmarkType.Blas)
        val commit2 = handler.fetchGitHistoryBySha(COMMIT_A.sha, 1, BenchmarkType.Blas)

        assertSame(commit1, commit2)
    }

    @Test
    fun `checks for same object reference for available branches`() {
        val branches1 = handler.getAvailableBranches()
        val branches2 = handler.getAvailableBranches()

        assertSame(branches1, branches2)
    }

    @Test
    fun `checks for same number for number of pages`() {
        val branches1 = mockHandler.getAvailableBranches()
        val branches2 = mockHandler.getAvailableBranches()

        assertEquals(branches1, branches2)
    }
}