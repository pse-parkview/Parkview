package com.parkview.parkview.git

/**
 * Interface that provides access to repository for fetching new commits.
 * This allow for updating the history with new commits
 */
interface RepositoryHandler {
    /**
     * Returns the commits for a given branch as a List,
     * since it doesn't contain any benchmark results.
     *
     * @param branch name of branch
     * @return list of commits for
     */
    fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit>

    /**
     * Returns a list of all available branches
     *
     * @return list of branch names
     */
    fun getAvailableBranches(): List<String>
}