package com.parkview.parkview.git

interface History {
    fun getBranch(name: String, device: String, benchmark: String): Branch
}