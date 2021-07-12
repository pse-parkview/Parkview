package com.parkview.parkview.git

/**
 * Class that represent a branch for a benchmark type.
 * Benchmark is fixed since results can't be compared
 * across benchmarks.
 *
 * @param name name of branch
 * @param benchmark name of benchmark type
 * @param commits list of commits contained in this branch
 */
class BranchForBenchmark(
    val name: String,
    val benchmark: Benchmark,
    private val commits: List<Commit> = emptyList()
) {
    /**
     * returns the commit for the given sha
     *
     * @param sha given sha for wanted commit
     * @return the wanted commit. If the commits does not exist, null is returned
     */
    fun getCommit(sha: String) = commits.find { it.sha == sha }

    /**
     * Returns this branches commits as a list
     *
     * @return list of [commits][Commit]
     */
    fun toList(): List<Commit> = commits
}