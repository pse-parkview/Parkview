package com.parkview.parkview.rest

import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler

/**
 * Implements RepositoryHandler by using the GitHub Api
 */
class GitApiHandler : RepositoryHandler {
    override fun fetchGitHistory(branch: String): List<Commit> {
        TODO("Not yet implemented")
    }
}