package com.parkview.parkview

import com.parkview.parkview.database.AnnotatingRepositoryHandler
import com.parkview.parkview.database.CachingDatabaseHandler
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.exposed.ExposedHandler
import com.parkview.parkview.git.CachingRepositoryHandler
import com.parkview.parkview.git.RepositoryHandler
import com.parkview.parkview.rest.GitApiHandler
import com.parkview.parkview.rest.ParkviewApiHandler
import com.parkview.parkview.tracking.PerformanceTracker
import com.parkview.parkview.tracking.SimpleTerminalHook
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(AppConfig::class)
class ParkviewApplication {
    @Bean
    fun databaseHandler(appConfig: AppConfig): DatabaseHandler = CachingDatabaseHandler(
        ExposedHandler(
            if (appConfig.database.embedded) {
                EmbeddedPostgres.start().postgresDatabase
            } else {
            HikariDataSource(HikariConfig()
                .apply {
                    jdbcUrl = appConfig.database.datasource.jdbcUrl
                    username = appConfig.database.datasource.username
                    password = appConfig.database.datasource.password
                }
            )
            },
        ),
        maxCached = appConfig.database.maxCached,
    )

    @Bean
    fun repositoryHandler(appConfig: AppConfig, databaseHandler: DatabaseHandler): RepositoryHandler =
        AnnotatingRepositoryHandler(
            CachingRepositoryHandler(
                GitApiHandler(
                    appConfig.gitApi.repoName,
                    appConfig.gitApi.owner,
                    appConfig.gitApi.firstCommitSha,
                    appConfig.gitApi.username,
                    appConfig.gitApi.token,
                ),
                maxCached = appConfig.gitApi.maxCached,
                branchLifetime = appConfig.gitApi.branchLifetime,
                branchListLifetime = appConfig.gitApi.branchListLifetime,
                shaLifetime = appConfig.gitApi.shaLifetime,
            ),
            databaseHandler
        )

    @Bean
    fun restHandler(repositoryHandler: RepositoryHandler, databaseHandler: DatabaseHandler, performanceTracker: PerformanceTracker) =
        ParkviewApiHandler(repositoryHandler, databaseHandler, performanceTracker)

    @Bean
    fun performanceTracker(repositoryHandler: RepositoryHandler, databaseHandler: DatabaseHandler) =
        PerformanceTracker(databaseHandler, repositoryHandler)
            .apply { addWebhook(SimpleTerminalHook()) }
}

fun main(args: Array<String>) {
    runApplication<ParkviewApplication>(*args)
}
