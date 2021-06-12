package com.parkview.parkview.git

class Branch(
    val name: String,
    val device: String,
    val benchmark: String,
    private val commits: List<Commit> = emptyList()
) {
    fun getCommit(sha: String): Commit {
        TODO("Not yet implemented")
    }
    fun toList(): List<Commit> {
        TODO("Not yet implemented")
    }
}