package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler

class AnnotatingRepositoryHandler(
    private val repHandler: RepositoryHandler,
    private val databaseHandler: DatabaseHandler,
) : RepositoryHandler {
    override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        repHandler.fetchGitHistory(branch, page, benchmarkType).map {
            it.copy()
                .apply {
                    for (device in databaseHandler.getAvailableDevicesForCommit(this, benchmarkType)) addDevice(device)
                }
        }

    override fun getAvailableBranches(): List<String> = repHandler.getAvailableBranches()

    override fun getNumberOfPages(branch: String): Int = repHandler.getNumberOfPages(branch)
}