package com.parkview.parkview.database

import com.parkview.parkview.git.Branch
import com.parkview.parkview.git.History

/**
 * Class that offers access to a history stored in a database
 *
 * @param database [DatabaseHandler] for handling access to database
 */
class HistoryDatabase(
    private val database: DatabaseHandler
): History {
    override fun getBranch(name: String, benchmark: String): Branch {
        TODO("Not yet implemented")
    }
}