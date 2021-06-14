package com.parkview.parkview.git

/**
 * Interface that provides access to repository for fetching new commits
 */
interface RepositoryHandler {
    /**
     * Returns the commits for a given branch
     *
     * @param branch name of branch
     * @return list of commits
     */
    fun fetchGitHistory(branch: String): List<Commit>
}