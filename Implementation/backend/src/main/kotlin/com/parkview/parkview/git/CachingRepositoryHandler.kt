package com.parkview.parkview.git

import java.util.*

private data class CachedBranch(
    val name: String,
    val fetchDate: Date,
    val commits: List<Commit>,
    val page: Int,
)

class CachingRepositoryHandler(
    private val handler: RepositoryHandler,
    private val maxCached: Int = 10,
    private val minutesSinceLastGitHistoryFetch: Int = 1,
    private val minutesSinceLastAvailableBranchesFetch: Int = 5,
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
        if ((Date().time - wantedBranch.fetchDate.time) / (1000 * 60) > minutesSinceLastGitHistoryFetch ) {
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
        if ((Date().time - availableBranchesFetchDate.time) / (1000 * 60) > minutesSinceLastAvailableBranchesFetch ) {
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