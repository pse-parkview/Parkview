package com.parkview.parkview.git

class CachingDecorator(
    val component: History,
    private val lastUsedBranches: List<Branch> = emptyList()
): History {
    override fun getBranch(name: String, device: String, benchmark: String): Branch {
        TODO("Not yet implemented")
    }
}