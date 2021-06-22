package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.BenchmarkResultStorage

/**
 * This class offers storage of benchmark results by using a database.
 * Access to the database is provided by a [DatabaseHandler] object.
 */
class BenchmarkResultDatabase(
    private val databaseHandler: DatabaseHandler
): BenchmarkResultStorage {
    override fun storeBenchmarkResults(results: List<BenchmarkResult>) {
        TODO("Not yet implemented")
    }
}