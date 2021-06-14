package com.parkview.parkview.git

/**
 * Class that represent a branch for a given device and benchmark type
 *
 * @param name name of branch
 * @param device name of device
 * @param benchmark name of benchmark type
 * @param commits list of commits contained in this branch
 */
class Branch(
    val name: String,
    val benchmark: String,
    private val commits: List<Commit> = emptyList()
) {
    /**
     * returns the commit for the given sha
     * TODO: Maybe throw exception if commit doesn't exist in branch?
     *
     * @param sha given sha for wanted commit
     * @return the wanted commit. If the commits does not exist, null is returned
     */
    fun getCommit(sha: String): Commit? {
        TODO("Not yet implemented")
    }

    /**
     * Returns this branches commits as a list
     *
     * @return list of [commits][Commit]
     */
    fun toList(): List<Commit> {
        TODO("Not yet implemented")
    }
}