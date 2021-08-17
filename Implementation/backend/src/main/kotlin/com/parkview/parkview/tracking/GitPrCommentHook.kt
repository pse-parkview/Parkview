package com.parkview.parkview.tracking

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.RepositoryHandler

class GitPrCommentHook(
    private val repositoryHandler: RepositoryHandler,
) : Webhook {
    private var commentsBySha = mutableMapOf<String, MutableList<String>>()

    override fun addResult(new: BenchmarkResult, previous: BenchmarkResult) {
        var comment: List<String> = listOf(
            "## ${new.benchmark} on ${new.device.name}",
            "| algorithm | new summary value | previous summary value | improvement |",
            "|-|-|-|-|",
        )
        for ((algorithm, value) in new.summaryValues) {
            if (!previous.summaryValues.containsKey(algorithm)) continue

            val difference = value - previous.summaryValues[algorithm]!!
            val trendString = when {
                difference < 0 -> {
                    "- $difference"
                }
                difference > 0 -> {
                    "+ $difference"
                }
                else -> {
                    "no change"
                }
            }

            comment += "|$algorithm|$value|${previous.summaryValues[algorithm]}|$trendString|"
        }

        commentsBySha.getOrPut(new.commit.sha) { mutableListOf("# Summary Values") }.add(comment.joinToString("\n"))
    }

    override fun notifyHook() {
        for ((sha, comment) in commentsBySha) {
            repositoryHandler.getPullRequestNumber(sha).forEach {
                repositoryHandler.commentIssue(it, comment.joinToString("\n"))
            }
        }
        commentsBySha = mutableMapOf()
    }
}