package com.parkview.parkview.git

/**
 * Decorator for History class, enables for caching of previous requests.
 *
 * @param component object that gets decorated
 */
class CachingDecorator(
    private val component: History,
): History {
    private val lastUsedBranches: List<Branch> = emptyList()

    override fun getBranch(name: String, benchmark: String): Branch {
        TODO("Not yet implemented")
    }
}