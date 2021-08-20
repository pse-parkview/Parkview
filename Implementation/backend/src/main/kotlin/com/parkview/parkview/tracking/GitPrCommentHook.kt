package com.parkview.parkview.tracking

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.Gson
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.RepositoryException
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

@JsonIgnoreProperties(ignoreUnknown = true)
private data class PullRequestModel(
    val number: Int,
    val state: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class IssueCommentModel(
    val body: String,
)

private interface GitHubHookService {
    @Headers("Accept: application/vnd.github.groot-preview+json")
    @GET("https://api.github.com/repos/{owner}/{repoName}/commits/{sha}/pulls")
    fun getPr(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("sha") firstSha: String,
    ): Call<List<PullRequestModel>>

    @POST("https://api.github.com/repos/{owner}/{repoName}/issues/{issueNumber}/comments")
    fun postIssueComment(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("issueNumber") issueNumber: Int,
        @Body comment: IssueCommentModel,
    ): Call<IssueCommentModel>
}

/**
 * [Webhook] that posts a comment to an open pull request for the given commits
 *
 * @param owner owner of the repository where the comments should be posted
 * @param repoName name of the repository where the comments should be posted
 * @param client [OkHttpClient] used for making api calls
 */
class GitPrCommentHook(
    private val owner: String,
    private val repoName: String,
    client: OkHttpClient,
) : Webhook {
    private val githubApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(GitHubHookService::class.java)

    private var commentsBySha = mutableMapOf<String, MutableList<String>>()

    override fun addResult(new: BenchmarkResult, previous: BenchmarkResult) {
        val comment: MutableList<String> = mutableListOf(
            "## ${new.benchmark} on ${new.device.name}",
            "| algorithm | new summary value | previous summary value | improvement |",
            "|-|-|-|-|",
        )
        for ((algorithm, value) in new.summaryValues) {
            if (!previous.summaryValues.containsKey(algorithm)) continue

            val difference = value - previous.summaryValues[algorithm]!!
            val trendString = when {
                difference < 0 -> {
                    "- ${difference * -1}"
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
            getPullRequestNumber(sha).forEach {
                commentIssue(it, comment.joinToString("\n"))
            }
        }
        commentsBySha.clear()
    }

    private fun getPullRequestNumber(sha: String): List<Int> {
        val prInfoResponse = githubApi.getPr(owner, repoName, sha).execute()

        if (!prInfoResponse.isSuccessful) {
            throw RepositoryException("Error while retrieving pull request number: ${prInfoResponse.errorBody()}")
        }

        val prInfo = prInfoResponse.body() ?: throw RepositoryException("Error while retrieving pull request number")

        return prInfo.filter { it.state == "open" }.map { it.number }
    }

    private fun commentIssue(issueNumber: Int, comment: String) {
        val response = githubApi.postIssueComment(owner, repoName, issueNumber, IssueCommentModel(comment)).execute()
        if (!response.isSuccessful) {
            println(response.code())
            println(response.raw().message)
            println(response.raw().body)
            throw RepositoryException("Error while posting comment: ${Gson().toJson(response.body())}")
        }
    }
}
