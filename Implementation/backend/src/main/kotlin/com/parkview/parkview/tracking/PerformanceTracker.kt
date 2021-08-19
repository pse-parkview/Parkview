package com.parkview.parkview.tracking

import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.RepositoryHandler

/**
 * Class that deals with the notification of webhooks
 *
 * @param databaseHandler [DatabaseHandler] used for retrieving benchmark information
 * @param repositoryHandler [RepositoryHandler] used for retrieving information about the git history
 * @param pageLimit maximum number of pages to search for previous commit with data before stopping
 * @param webhooks list of [Webhooks][Webhook]
 */
class PerformanceTracker(
    private val databaseHandler: DatabaseHandler,
    private val repositoryHandler: RepositoryHandler,
    private val pageLimit: Int = 5,
    private val webhooks: MutableList<Webhook> = mutableListOf(),
) {
    /**
     * Adds a new webhook
     *
     * @param webhook new webhook
     */
    fun addWebhook(webhook: Webhook) {
        webhooks.add(webhook)
    }

    /**
     * Goes through each result and notifies the webhooks.
     *
     * @param results list of [BenchmarkResults][BenchmarkResult]
     */
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