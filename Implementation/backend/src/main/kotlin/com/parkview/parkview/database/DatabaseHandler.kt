package com.parkview.parkview.database

import com.parkview.parkview.git.*

/**
 * Interface for accessing a database. It offers methods for
 * storing, updating and retrieving commits and benchmark results
 */
interface DatabaseHandler {
    /**
     * Updates existing commits in the database with the ones given as a
     * parameter or adds them to the database if they dont exist yet. If a commit
     * with the same sha as a given Commit already exists, the commit in the database
     * gets replaced by the given commit.
     *
     * @param commits list of [commits][Commit] to update
     */
    fun updateCommits(commits: List<Commit>)

    /**
     * Updates existing benchmark results in the database with the ones given as a
     * parameter or adds them to the database if they dont exist yet. If a benchmark result
     * for the same commit, device, benchmark, problem setup and time already exists, it gets replaced.
     *
     * @param results list of [benchmark results][BenchmarkResult] to update
     */
    fun updateBenchmarkResults(results: List<BenchmarkResult>)

    /**
     * Fetches all commits for a given branch and benchmark type
     * and packs them into a branch object. The commits contain their corresponding
     * benchmark results for every device available.
     *
     * @param branch name of wanted branch
     * @param benchmark wanted benchmark type
     *
     * @return [BranchForBenchmark] object for the wanted branch and benchmark
     * @throws MissingBranchException if the wanted branch is not available
     */
    fun fetchBranch(branch: String, benchmark: Benchmark): BranchForBenchmark

    /**
     * Fetches a single benchmark result for the given commit, device and
     * benchmark type.
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
