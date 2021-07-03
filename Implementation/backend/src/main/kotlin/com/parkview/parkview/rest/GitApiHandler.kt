package com.parkview.parkview.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler
import org.springframework.web.client.RestTemplate
import java.lang.Exception
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

/**
 * Implements RepositoryHandler by using the GitHub Api
 */
class GitApiHandler(
    private val repoName: String,
    private val owner: String,
) : RepositoryHandler {
    override fun fetchGitHistory(branch: String): List<Commit> {
        val uri = "https://api.github.com/repos/$owner/$repoName/commits?sha=$branch";

        val restTemplate = RestTemplate();
        val result = restTemplate.getForObject(uri, Array<CommitModel>::class.java)?.toList() ?: throw Exception() // TODO: replace with fitting exception

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
}