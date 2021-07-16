package com.parkview.parkview.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler
import org.springframework.web.client.RestTemplate
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
private data class CommitModel(
    val sha: String,
    val commit: CommitInfoModel,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class CommitInfoModel(
    val author: AuthorModel,
    val message: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class AuthorModel(
    val name: String,
    val date: Date,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class BranchInfoModel(
    val name: String,
)

class GitApiException(message: String) : Exception(message)

/**
 * Implements RepositoryHandler by using the GitHub Api
 */
class GitApiHandler(
    private val repoName: String,
    private val owner: String,
) : RepositoryHandler {
    override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> {
        val uri =
            "https://api.github.com/repos/$owner/$repoName/commits?sha=$branch&page=$page"

        val restTemplate = RestTemplate()
        val result = restTemplate.getForObject(uri, Array<CommitModel>::class.java)?.toList()
            ?: throw GitApiException("Error while parsing git history response")

        val commits = mutableListOf<Commit>()

        for (model in result) {
            commits += Commit(
                sha = model.sha,
                message = model.commit.message,
                date = model.commit.author.date,
                author = model.commit.author.name,
            )
        }

        return commits
    }

    override fun getAvailableBranches(): List<String> {
        val uri = "https://api.github.com/repos/$owner/$repoName/branches"

        val restTemplate = RestTemplate()
        val result = restTemplate.getForObject(uri, Array<BranchInfoModel>::class.java)?.toList()
            ?: throw GitApiException("Error while parsing branch list response")

        return result.map { branch -> branch.name }
    }
}