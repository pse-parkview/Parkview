package com.parkview.parkview

import com.parkview.parkview.benchmark.GsonBenchmarkJsonParser
import com.parkview.parkview.database.AnnotatingRepositoryHandler
import com.parkview.parkview.database.CachingDatabaseHandler
import com.parkview.parkview.database.DatabaseHandler
import com.parkview.parkview.database.exposed.ExposedHandler
import com.parkview.parkview.git.CachingRepositoryHandler
import com.parkview.parkview.git.RepositoryHandler
import com.parkview.parkview.rest.GitApiHandler
import com.parkview.parkview.rest.ParkviewApiHandler
import com.parkview.parkview.tracking.GitPrCommentHook
import com.parkview.parkview.tracking.PerformanceTracker
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
                HikariDataSource(
                    HikariConfig()
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
    fun repositoryHandler(
        appConfig: AppConfig,
        databaseHandler: DatabaseHandler,
        okHttpClient: OkHttpClient,
    ): RepositoryHandler =
        AnnotatingRepositoryHandler(
            CachingRepositoryHandler(
                GitApiHandler(
                    appConfig.gitApi.repoName,
                    appConfig.gitApi.owner,
                    appConfig.gitApi.firstCommitSha,
                    okHttpClient,
                ),
                maxCached = appConfig.gitApi.maxCached,
                branchLifetime = appConfig.gitApi.branchLifetime,
                branchListLifetime = appConfig.gitApi.branchListLifetime,
                shaLifetime = appConfig.gitApi.shaLifetime,
            ),
            databaseHandler
        )

    @Bean
    fun restHandler(
        repositoryHandler: RepositoryHandler,
        databaseHandler: DatabaseHandler,
        performanceTracker: PerformanceTracker,
    ): ParkviewApiHandler =
        ParkviewApiHandler(repositoryHandler, databaseHandler, performanceTracker, GsonBenchmarkJsonParser())

    @Bean
    fun performanceTracker(
        appConfig: AppConfig,
        repositoryHandler: RepositoryHandler,
        databaseHandler: DatabaseHandler,
        okHttpClient: OkHttpClient,
    ) =
        PerformanceTracker(databaseHandler, repositoryHandler)
            .apply {
                if (appConfig.performanceTracker.commentHookEnabled)
                    addWebhook(
                        GitPrCommentHook(
                            appConfig.gitApi.owner,
                            appConfig.gitApi.repoName,
                            okHttpClient,
                        )
                    )
            }

    @Bean
    fun okHttpClient(appConfig: AppConfig) =
        OkHttpClient.Builder()
            .addInterceptor(
                // taken from https://gist.github.com/seccomiro/85446c4849855615d1938133bce30738
                object : Interceptor {
                    private val credentials: String =
                        Credentials.basic(appConfig.gitApi.username, appConfig.gitApi.token)

                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                        val authenticatedRequest = request.newBuilder()
                            .header("Authorization", credentials).build()
                        return chain.proceed(authenticatedRequest)
                    }
                }
            ).build()
}

fun main(args: Array<String>) {
    runApplication<ParkviewApplication>(*args)
}
