package com.parkview.parkview.git

class RepositoryException(message: String) : Exception(message)

/**
 * Interface that provides access to repository for fetching new commits.
 * This allow for updating the history with new commits
 */
interface RepositoryHandler {
    /**
     * Returns the commits for a given branch as a List,
     * since it doesn't contain any benchmark results.
     *
     * @param branch sha of commit or name of branch
     * @return list of commits for
     */
    fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit>

    fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit>

    /**
     * Returns a list of all available branches
     *
     * @return list of branch names
     */
    fun getAvailableBranches(): List<String>

    fun getNumberOfPages(branch: String): Int
}