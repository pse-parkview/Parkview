package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler

class AnnotatingRepositoryHandler(
    private val repHandler: RepositoryHandler,
    private val databaseHandler: DatabaseHandler,
) : RepositoryHandler {
    override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        val commits = repHandler.fetchGitHistory(branch, page, benchmarkType)

        for (commit in commits) {
            val devices = databaseHandler.getAvailableDevices(
                commit,
                benchmark = benchmarkType,
            )
            for (device in devices) {
                commit.addDevice(device)
            }
        }

        return commits
    }

    override fun getAvailableBranches(): List<String> = repHandler.getAvailableBranches()
}