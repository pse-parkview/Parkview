package com.parkview.parkview.tracking

import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.RepositoryHandler

class PerformanceTracker(
    private val databaseHandler: DatabaseHandler,
    private val repositoryHandler: RepositoryHandler,
    private val pageLimit: Int = 5,
    private val webhooks: MutableList<Webhook> = mutableListOf(),
) {
    fun addWebhook(webhook: Webhook) {
        webhooks.add(webhook)
    }

    fun notifyHooks(results: List<BenchmarkResult>) {
        for (result in results) {
            // fetch from database since you can post incomplete data to update the entry in the database
            val new = databaseHandler.fetchBenchmarkResult(result.commit, result.device, result.benchmark)
            val prev = findPreviousResult(result) ?: continue

            webhooks.forEach { it.addResult(new, prev) }
        }

        webhooks.forEach { it.notifyHook() }
    }

    private fun findPreviousResult(result: BenchmarkResult): BenchmarkResult? {
        var page = 1
        var commits = repositoryHandler.fetchGitHistoryBySha(result.commit.sha, page, result.benchmark)

        while ((page <= pageLimit) and commits.isNotEmpty()) {
            for (commit in commits) {
                if (commit.sha == result.commit.sha) continue
                if (databaseHandler.hasDataAvailable(commit, result.device, result.benchmark)) {
                    return databaseHandler.fetchBenchmarkResult(commit, result.device, result.benchmark)
                }
            }

            page++
            commits = repositoryHandler.fetchGitHistoryBySha(result.commit.sha, page, result.benchmark)
        }

        return null
    }
}