package com.parkview.parkview.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import kotlin.math.ceil

// taken from https://gist.github.com/seccomiro/85446c4849855615d1938133bce30738
private class BasicAuthInterceptor(user: String, password: String) : Interceptor {
    private val credentials: String = Credentials.basic(user, password)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}

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

@JsonIgnoreProperties(ignoreUnknown = true)
private data class HeadInfoModel(
    @SerializedName("object") val objectInfo: ObjectInfoModel,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ObjectInfoModel(
    val sha: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class DiffInfoModel(
    val total_commits: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class PullRequestModel(
    val number: Int,
    val state: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class IssueCommentModel(
    val body: String,
)

class GitApiException(message: String) : Exception(message)

private interface GitHubService {
    @GET("repos/{owner}/{repoName}/commits")
    fun fetchHistory(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Query("sha") branch: String,
        @Query("page") page: Int,
    ): Call<List<CommitModel>>

    @GET("repos/{owner}/{repoName}/branches")
    fun getBranches(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Query("page") page: Int,
    ): Call<List<BranchInfoModel>>

    @GET("repos/{owner}/{repoName}/git/refs/heads/{branch}")
    fun getHeadInfo(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("branch") branch: String,
    ): Call<HeadInfoModel>


    @GET("https://api.github.com/repos/{owner}/{repoName}/compare/{firstSha}...{lastSha}")
    fun getDiff(
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("firstSha") firstSha: String,
        @Path("lastSha") lastSha: String,
    ): Call<DiffInfoModel>

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
 * Implements RepositoryHandler by using the GitHub Api
 *
 * @param repoName name of repository
 * @param owner owner of repository
 * @param username github account username for auth
 * @param token github account auth token for auth
 */
class GitApiHandler(
    private val repoName: String,
    private val owner: String,
    private val firstCommitSha: String,
    private val username: String = "",
    private val token: String = "",
) : RepositoryHandler {
    private val client = OkHttpClient.Builder()
        .addInterceptor(
            BasicAuthInterceptor(
                username,
                token
            )
        ).build()

    private val service = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val githubApi = service.create(GitHubService::class.java)

    private val commitPerPage = 30

    override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        githubApi.fetchHistory(owner, repoName, branch, page).execute().body()
            ?.map { Commit(it.sha, it.commit.message, it.commit.author.date, it.commit.author.name) }
            ?: throw GitApiException("Error while retrieving history")

    override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        githubApi.fetchHistory(owner, repoName, rev, page).execute().body()
            ?.map { Commit(it.sha, it.commit.message, it.commit.author.date, it.commit.author.name) }
            ?: throw GitApiException("Error while retrieving history")

    override fun getAvailableBranches(): List<String> {
        var page = 1

        val branches = mutableListOf<String>()
        var request = githubApi.getBranches(owner, repoName, page).execute().body()?.map { it.name }
                ?: throw GitApiException("Error while retrieving available branches")
        while (request.isNotEmpty()) {
            branches += request
            page++
            request = githubApi.getBranches(owner, repoName, page).execute().body()?.map { it.name }
                ?: throw GitApiException("Error while retrieving available branches")
        }

        return branches
    }

    override fun getNumberOfPages(branch: String): Int {
        val headInfo = githubApi.getHeadInfo(owner, repoName, branch).execute().body()
            ?: throw GitApiException("Error while retrieving branch head")

        val diffInfo =
            githubApi.getDiff(owner, repoName, firstCommitSha, headInfo.objectInfo.sha).execute().body()
                ?: throw GitApiException("Error while retrieving diff")

        return ceil((diffInfo.total_commits + 1) / commitPerPage.toDouble()).toInt()
    }

    override fun getPullRequestNumber(sha: String): List<Int> {
        val prInfoResponse = githubApi.getPr(owner, repoName, sha).execute()

        if (!prInfoResponse.isSuccessful) {
            throw GitApiException("Error while retrieving pull request number: ${prInfoResponse.errorBody()}")
        }

        val prInfo = prInfoResponse.body() ?: throw GitApiException("Error while retrieving pull request number")

        return prInfo.filter { it.state == "open" }.map { it.number }
    }

    override fun commentIssue(issueNumber: Int, comment: String) {
        val response = githubApi.postIssueComment(owner, repoName, issueNumber, IssueCommentModel(comment)).execute()
        if (!response.isSuccessful) {
            println(response.code())
            println(response.raw().message)
            println(response.raw().body)
            throw GitApiException("Error while posting comment: ${Gson().toJson(response.body())}")
        }
    }
}