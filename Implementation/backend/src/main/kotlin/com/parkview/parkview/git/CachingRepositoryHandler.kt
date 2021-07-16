package com.parkview.parkview.git

import java.util.*

private data class CachedBranch(
    val name: String,
    val fetchDate: Date,
    val commits: List<Commit>,
    val page: Int,
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

    override fun fetchGitHistory(branch: String, page: Int): List<Commit> {
        val wantedBranch = branchCache.find { (it.name == branch) && (it.page == page) }

        // branch is not cached
        if (wantedBranch == null) {
            val newBranch = handler.fetchGitHistory(branch, page)
            addToCache(
                CachedBranch(
                    name = branch,
                    fetchDate = Date(),
                    commits = newBranch,
                    page = page,
                )
            )

            return newBranch
        }

        // too old
        if ((Date().time - wantedBranch.fetchDate.time) / (1000 * 60) > branchLifetime) {
            val newBranch = handler.fetchGitHistory(branch, page)
            branchCache.remove(wantedBranch)
            addToCache(
                CachedBranch(
                    name = branch,
                    fetchDate = Date(),
                    commits = newBranch,
                    page = page,
                )
            )

            return newBranch

        }

        // hit
        return wantedBranch.commits
    }

    override fun getAvailableBranches(): List<String> {
        if ((Date().time - availableBranchesFetchDate.time) / (1000 * 60) > branchListLifetime) {
            availableBranches = handler.getAvailableBranches()
            availableBranchesFetchDate = Date()
        }
        return availableBranches
    }

    private fun addToCache(branch: CachedBranch) {
        branchCache.add(branch)

        if (branchCache.size > maxCached) {
            branchCache.removeAt(0)
        }
    }
}