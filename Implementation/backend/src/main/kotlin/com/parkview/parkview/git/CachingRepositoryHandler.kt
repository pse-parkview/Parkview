package com.parkview.parkview.git

import java.util.*

private data class CachedBranch(
    val name: String,
    val benchmark: BenchmarkType,
    val fetchDate: Date,
    val pages: MutableMap<Int, List<Commit>>,
    val numberPages: Int,
)

private data class CachedSha(
    val name: String,
    val benchmark: BenchmarkType,
    val fetchDate: Date,
    val pages: MutableMap<Int, List<Commit>>,
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
    private val maxCached: Int = 20,
    private val branchLifetime: Int = 5,
    private val branchListLifetime: Int = 5,
    private val shaLifetime: Int = 2,
) : RepositoryHandler {
    private val branchCache = mutableListOf<CachedBranch>()
    private val shaCache = mutableListOf<CachedSha>()
    private var availableBranches = handler.getAvailableBranches()
    private var availableBranchesFetchDate = Date()

    override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        getOrPutCache(branch, benchmarkType, page).pages.getOrPut(page) {
            handler.fetchGitHistoryByBranch(branch,
                page,
                benchmarkType)
        }

    override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        val wantedSha = shaCache.find { (it.name == rev) and (it.benchmark == benchmarkType) }

        // rev is not cached
        if (wantedSha == null) {
            val newBranch = handler.fetchGitHistoryByBranch(rev, page, benchmarkType)
            addToShaCache(
                CachedSha(
                    rev,
                    benchmarkType,
                    Date(),
                    mutableMapOf(page to newBranch),
                )
            )

            return newBranch
        }

        // too old
        if ((Date().time - wantedSha.fetchDate.time) / (1000 * 60) > shaLifetime) {
            val newBranch = handler.fetchGitHistoryByBranch(rev, page, benchmarkType)
            shaCache.remove(wantedSha)
            addToShaCache(
                CachedSha(
                    rev,
                    benchmarkType,
                    Date(),
                    mutableMapOf(page to newBranch),
                )
            )

            return newBranch

        }

        // hit
        shaCache.remove(wantedSha)
        shaCache.add(wantedSha)
        return wantedSha.pages.getOrPut(page) { handler.fetchGitHistoryByBranch(rev, page, benchmarkType) }
    }

    override fun getAvailableBranches(): List<String> {
        if ((Date().time - availableBranchesFetchDate.time) / (1000 * 60) > branchListLifetime) {
            availableBranches = handler.getAvailableBranches()
            availableBranchesFetchDate = Date()
        }

        return availableBranches
    }

    override fun getNumberOfPages(branch: String): Int = getOrPutCache(branch).numberPages

    private fun getOrPutCache(
        branch: String,
        benchmarkType: BenchmarkType = BenchmarkType.values().first(),
        page: Int = 1,
    ): CachedBranch {
        val wantedBranch = branchCache.find { (it.name == branch) and (it.benchmark == benchmarkType) }

        // branch is not cached
        if (wantedBranch == null) {
            val newBranch = CachedBranch(branch,
                benchmarkType,
                Date(),
                mutableMapOf(page to handler.fetchGitHistoryByBranch(branch, page, benchmarkType)),
                handler.getNumberOfPages(branch))
            addToBranchCache(newBranch)

            return newBranch
        }

        // too old
        if ((Date().time - wantedBranch.fetchDate.time) / (1000 * 60) > branchLifetime) {
            branchCache.remove(wantedBranch)
            val newBranch = CachedBranch(branch,
                benchmarkType,
                Date(),
                mutableMapOf(page to handler.fetchGitHistoryByBranch(branch, page, benchmarkType)),
                handler.getNumberOfPages(branch))
            addToBranchCache(newBranch)

            return newBranch
        }

        // hit
        branchCache.remove(wantedBranch)
        branchCache.add(wantedBranch)
        return wantedBranch
    }

    private fun addToBranchCache(branch: CachedBranch) {
        branchCache.add(branch)

        if (branchCache.size > maxCached) {
            branchCache.removeAt(0)
        }
    }

    private fun addToShaCache(sha: CachedSha) {
        shaCache.add(sha)

        if (shaCache.size > maxCached) {
            shaCache.removeAt(0)
        }
    }
}