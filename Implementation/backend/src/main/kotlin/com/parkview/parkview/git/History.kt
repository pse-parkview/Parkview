package com.parkview.parkview.git

/**
 * Class that represents an entire git history. It allows for retrieving branches
 * for a given benchmark type, therefore containing the results of the benchmarks
 * run on this branch.
 */
interface History {
    /**
     * Returns the branch with the given name for a given device and benchmark type
     *
     * @param name name of branch
     * @param benchmark type of benchmark
     *
     * @return branch object
     */
    fun getBranch(name: String, benchmark: Benchmark): BranchForBenchmark
}