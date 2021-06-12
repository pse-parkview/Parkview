package com.parkview.parkview.git

interface RepositoryHandler {
    fun fetchGitHistory(branch: String): List<Commit>
}