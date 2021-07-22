package com.parkview.parkview.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryHandler
import org.springframework.http.HttpHeaders
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
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

class GitApiException(message: String) : Exception(message)

private interface GitHubService {
    @GET("/repos/{owner}/{repoName}/commits")
    fun fetchHistory(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Query("sha") branch: String,
        @Query("page") page: Int,
    ): Call<List<CommitModel>>

    @GET("https://api.github.com/repos/{owner}/{repoName}/branches")
    fun getBranches(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
    ): Call<List<BranchInfoModel>>

    @GET("https://api.github.com/repos/{owner}/{repoName}/git/refs/heads/{branch}")
    fun getHeadInfo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("branch") branch: String,
    ): Call<HeadInfoModel>


    @GET("https://api.github.com/repos/{owner}/{repoName}/compare/{firstSha}...{lastSha}")
    fun getDiff(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repoName") repoName: String,
        @Path("firstSha") firstSha: String,
        @Path("lastSha") lastSha: String,
    ): Call<DiffInfoModel>
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
    private val service = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val githubApi = service.create(GitHubService::class.java)

    private val commitPerPage = 30

    override fun fetchGitHistory(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        githubApi.fetchHistory(token, owner, repoName, branch, page).execute().body()
            ?.map { Commit(it.sha, it.commit.message, it.commit.author.date, it.commit.author.name) }
            ?: throw GitApiException("Error while retrieving history")

    override fun getAvailableBranches(): List<String> =
        githubApi.getBranches(token, owner, repoName).execute().body()?.map { it.name }
            ?: throw GitApiException("Error while retrieving available branches")

    override fun getNumberOfPages(branch: String): Int {
        val headInfo = githubApi.getHeadInfo(token, owner, repoName, branch).execute().body() ?: throw GitApiException("Error while retrieving branch head")

        val diffInfo = githubApi.getDiff(token, owner, repoName, firstCommitSha, headInfo.objectInfo.sha).execute().body()
            ?: throw GitApiException("Error while parsing git history response")

        return (diffInfo.total_commits + 1) / commitPerPage
    }
}