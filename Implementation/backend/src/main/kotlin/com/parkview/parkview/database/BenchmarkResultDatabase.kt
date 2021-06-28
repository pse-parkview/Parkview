package com.parkview.parkview.database

import com.parkview.parkview.benchmark.BenchmarkResultStorage
import com.parkview.parkview.git.BenchmarkResult

/**
 * This class offers storage of benchmark results by using a database.
 * Access to the database is provided by a [DatabaseHandler] object.
 *
 * @param databaseHandler [DatabaseHandler] for handling access to database
 */
class BenchmarkResultDatabase(
    private val databaseHandler: DatabaseHandler
) : BenchmarkResultStorage {
    override fun storeBenchmarkResults(results: List<BenchmarkResult>) {
        TODO("Not yet implemented")
    }
}