package com.parkview.parkview

data class AppConfig(
    val database: DatabaseConfig,
    val gitApi: GitApiConfig,
    val performanceTracker: PerformanceTrackerConfig = PerformanceTrackerConfig(),
)

data class GitApiConfig(
    val maxCached: Int,
    val branchLifetime: Int,
    val shaLifetime: Int,
    val branchListLifetime: Int,
    val repoName: String,
    val owner: String,
    val firstCommitSha: String,
    val username: String = "",
    val token: String = "",
)

data class DatabaseConfig(
    val datasource: DataSourceConfig,
    val maxCached: Int,
    val embedded: Boolean = false,
)

data class DataSourceConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
)

data class PerformanceTrackerConfig(
    val commentHookEnabled: Boolean = false,
)
