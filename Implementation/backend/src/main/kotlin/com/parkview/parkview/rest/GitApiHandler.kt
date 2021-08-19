package com.parkview.parkview.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.RepositoryException
import com.parkview.parkview.git.RepositoryHandler
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*
import kotlin.math.ceil


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
}

/**
 * Implements RepositoryHandler by using the GitHub Api
 *
 * @param repoName name of repository
 * @param owner owner of repository
 */
class GitApiHandler(
    private val repoName: String,
    private val owner: String,
    private val firstCommitSha: String,
    client: OkHttpClient,
) : RepositoryHandler {
    private val githubApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(GitHubService::class.java)

    private val commitPerPage = 30

    override fun fetchGitHistoryByBranch(branch: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        githubApi.fetchHistory(owner, repoName, branch, page).execute().body()
            ?.map { Commit(it.sha, it.commit.message, it.commit.author.date, it.commit.author.name) }
            ?: throw RepositoryException("Error while retrieving history")

    override fun fetchGitHistoryBySha(rev: String, page: Int, benchmarkType: BenchmarkType): List<Commit> =
        githubApi.fetchHistory(owner, repoName, rev, page).execute().body()
            ?.map { Commit(it.sha, it.commit.message, it.commit.author.date, it.commit.author.name) }
            ?: throw RepositoryException("Error while retrieving history")

    override fun getAvailableBranches(): List<String> {
        var page = 1

        val branches = mutableListOf<String>()
        var request = githubApi.getBranches(owner, repoName, page).execute().body()?.map { it.name }
            ?: throw RepositoryException("Error while retrieving available branches")
        while (request.isNotEmpty()) {
            branches += request
            page++
            request = githubApi.getBranches(owner, repoName, page).execute().body()?.map { it.name }
                ?: throw RepositoryException("Error while retrieving available branches")
        }

        return branches
    }

    override fun getNumberOfPages(branch: String): Int {
        val headInfo = githubApi.getHeadInfo(owner, repoName, branch).execute().body()
            ?: throw RepositoryException("Error while retrieving branch head")

        val diffInfo =
            githubApi.getDiff(owner, repoName, firstCommitSha, headInfo.objectInfo.sha).execute().body()
                ?: throw RepositoryException("Error while retrieving diff")

        return ceil((diffInfo.total_commits + 1) / commitPerPage.toDouble()).toInt()
    }
}