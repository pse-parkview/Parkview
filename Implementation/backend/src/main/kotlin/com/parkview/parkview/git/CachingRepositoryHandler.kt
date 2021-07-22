package com.parkview.parkview.git

import java.util.*

private data class CachedBranch(
    val name: String,
    val fetchDate: Date,
    val pages: MutableMap<Int, List<Commit>>,
    val numberPages: Int,
)

/**
 * Decorator for [RepositoryHandler], enables it to cache previous requests
 *
 * @param handler [RepositoryHandler] that gets decorated
 * @param maxCached maximum number of cached branches
 * @param branchLifetime lifetime of branch before it has to be refetched in minutes
 * @param branchListLifetime lifetime of branch list before it has to be refetched in minutes
 */
class CachingRepositoryHandler(
    private val handler: RepositoryHandler,
    private val maxCached: Int = 10,
    private val branchLifetime: Int = 5,
    private val branchListLifetime: Int = 5,
) : RepositoryHandler {
    private val branchCache = mutableListOf<CachedBranch>()
    private var availableBranches = handler.getAvailableBranches()
    private var availableBranchesFetchDate = Date()

    override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        val wantedBranch = branchCache.find { (it.name == branch) }

        // branch is not cached
        if (wantedBranch == null) {
            val newBranch = handler.fetchGitHistory(branch, page, benchmarkType)
            addToCache(CachedBranch(branch, Date(), mutableMapOf(page to newBranch), handler.getNumberOfPages(branch)))

            return newBranch
        }

        // too old
        if ((Date().time - wantedBranch.fetchDate.time) / (1000 * 60) > branchLifetime) {
            val newBranch = handler.fetchGitHistory(branch, page, benchmarkType)
            branchCache.remove(wantedBranch)
            addToCache(CachedBranch(branch, Date(), mutableMapOf(page to newBranch), handler.getNumberOfPages(branch)))

            return newBranch

        }

        // hit
        return wantedBranch.pages.getOrPut(page) { handler.fetchGitHistory(branch, page, benchmarkType) }
    }

    override fun getAvailableBranches(): List<String> {
        if ((Date().time - availableBranchesFetchDate.time) / (1000 * 60) > branchListLifetime) {
            availableBranches = handler.getAvailableBranches()
            availableBranchesFetchDate = Date()
        }
        return availableBranches
    }

    override fun getNumberOfPages(branch: String): Int =
        branchCache.find { (it.name == branch) }?.numberPages ?: handler.getNumberOfPages(branch)

    private fun addToCache(branch: CachedBranch) {
        branchCache.add(branch)

        if (branchCache.size > maxCached) {
            branchCache.removeAt(0)
        }
    }
}