package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Branch
import com.parkview.parkview.git.Commit

/**
 * Interface for accessing a database. It offers methods for
 * updating and retrieving commits and benchmark results
 */
interface DatabaseHandler {
    /**
     * Updates existing commits in the database with the ones given as a
     * parameter or adds them to the database if they dont exist yet.
     *
     * @param commits list of [commits][Commit] to update
     */
    fun updateCommits(commits: List<Commit>)

    /**
     * Updates existing benchmark results in the database with the ones given as a
     * parameter or adds them to the database if they dont exist yet.
     *
     * @param results list of [benchmark results][BenchmarkResult] to update
     */
    fun updateBenchmarkResults(results: List<BenchmarkResult>)

    /**
     * Fetches all commits for a given branch, device and benchmark type
     * and packs them into a branch object. The commits contain their corresponding
     * benchmark results.
     *
     * @param branch name of branch
     * @param benchmark name of benchmark type
     *
     * @return [Branch] object containing the commits, * name of branch, name of device and name of benchmark type
     */
    fun fetchBranch(branch: String, benchmark: String): Branch
}