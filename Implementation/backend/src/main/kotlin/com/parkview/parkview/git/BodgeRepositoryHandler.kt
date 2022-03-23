package com.parkview.parkview.git

import kotlinx.browser.window
import kotlinx.coroutines.*


// TODO: Think of a better name
/**
 * This class handles Repository Information by accessing a Github Repository containing
 * the need information. See https://github.com/pse-parkview/parkview-data
 *
 */
class BodgeRepositoryHandler(
    val repo_owner: String,
    val repo_name: String,
) : RepositoryHandler {
    override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        TODO("Not yet implemented")
    }

    override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        TODO("Not yet implemented")
    }

    override fun getAvailableBranches(): List<String> {
        val url = "https://raw.githubusercontent.com/$repo_owner/$repo_name/main/git_data/branches"
        println(url)
        var text: String? = null
        val job = GlobalScope.launch {
            launch {
                text = window.fetch(url).await().text().await()
            }.join()
        }

        while (!job.isCompleted) {}

        println(text)

        if (text == null)
            return emptyList()

        return listOf(text!!)
    }

    override fun getNumberOfPages(branch: String): Int {
        TODO("Not yet implemented")
    }
}