package com.parkview.parkview

import com.parkview.parkview.database.AnnotatingRepositoryHandler
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.exposed.ExposedHandler
import com.parkview.parkview.git.CachingRepositoryHandler
import com.parkview.parkview.git.RepositoryHandler
import com.parkview.parkview.rest.GitApiHandler
import com.parkview.parkview.rest.ParkviewApiHandler
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(AppConfig::class)
class ParkviewApplication {
    @Bean
    fun databaseHandler(appConfig: AppConfig): DatabaseHandler = ExposedHandler(
        HikariDataSource(HikariConfig()
            .apply {
                jdbcUrl = appConfig.datasource.jdbcUrl
                username = appConfig.datasource.username
                password = appConfig.datasource.password
            }
        )
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
            ),
            databaseHandler
        )

    @Bean
    fun restHandler(repositoryHandler: RepositoryHandler, databaseHandler: DatabaseHandler) =
        ParkviewApiHandler(repositoryHandler, databaseHandler)
}

fun main(args: Array<String>) {
    runApplication<ParkviewApplication>(*args)
}
