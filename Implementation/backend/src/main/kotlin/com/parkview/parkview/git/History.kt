package com.parkview.parkview.git

/**
 * Class that represents an entire git history
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
    fun getBranch(name: String, benchmark: String): Branch
}