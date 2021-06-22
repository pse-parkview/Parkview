package com.parkview.parkview.database

import com.parkview.parkview.git.*

/**
 * Interface for accessing a database. It offers methods for
 * updating and retrieving commits and benchmark results
 */
interface DatabaseHandler {
    /**
     * Updates existing commits in the database with the ones given as a
     * parameter or adds them to the database if they dont exist yet. If a Commit
     * with the same sha as a given Commit already exists, the commit in the database
     * gets replaced by the given commit.
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
     * Fetches all commits for a given branch and benchmark type
     * and packs them into a branch object. The commits contain their corresponding
     * benchmark results for every device available.
     *
     * @param branch name of branch
     * @param benchmark name of benchmark type
     *
     * @return [Branch] object containing the commits, * name of branch, name of device and name of benchmark type
     * @throws MissingBranchException if the wanted branch is not available
     */
    fun fetchBranch(branch: String, benchmark: Benchmark): Branch

    /**
     * Fetches a single commit from the database
     * TODO: add exception for missing commit
     *
     * @param sha sha of wanted commit
     * @return wanted commit
     *
     * @throws MissingCommitException if the wanted benchmark is not available
     */
    fun fetchCommit(sha: String): Commit

    /**
     * Fetches a single benchmark result for the given commit, device and
     * benchmark type.
     * TODO: add exception for missing benchmark result
     *
     * @param commit chosen commit
     * @param device type of device
     * @param benchmark type of benchmark
     * @return wanted benchmark result
     *
     * @throws MissingBenchmarkResultException if the wanted benchmark result is not available
     */
    fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: Benchmark): BenchmarkResult
}
