package com.parkview.parkview.git

/**
 * Class that represents an entire git history. It allows for retrieving branches
 * for a given benchmark type, therefore containing the results of the benchmarks
 * run on this branch.
 */
interface History {
    /**
     * Returns an up to date branch with the given name for a given benchmark type.
     *
     * @param name name of branch
     * @param benchmark type of benchmark
     *
     * @return branch object
     */
    fun getBranch(name: String, benchmark: Benchmark): BranchForBenchmark {
        if (!isUpToDate()) {
            updateHistory()
        }

        return getCurrentBranch(name, benchmark)
    }

    /**
     * Returns the wanted branch without checking for new commits.
     *
     * @param name name of branch
     * @param benchmark type of benchmark
     *
     * @return branch object
     */
    fun getCurrentBranch(name: String, benchmark: Benchmark): BranchForBenchmark

    /**
     * Updates the history.
     */
    fun updateHistory()

    /**
     * Checks whether or not the history is up to date.
     *
     * @return true if the history is up to date
     */
    fun isUpToDate(): Boolean
}