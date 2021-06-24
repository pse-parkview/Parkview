package com.parkview.parkview.database

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BranchForBenchmark
import com.parkview.parkview.git.History

/**
 * This class offers access to a git history stored in a database.
 * Access to the database is provided by a [DatabaseHandler] object.
 *
 * @param databaseHandler [DatabaseHandler] for handling access to database
 */
class HistoryDatabase(
    private val databaseHandler: DatabaseHandler
) : History {
    override fun getBranch(name: String, benchmark: Benchmark): BranchForBenchmark {
        TODO("Not yet implemented")
    }
}