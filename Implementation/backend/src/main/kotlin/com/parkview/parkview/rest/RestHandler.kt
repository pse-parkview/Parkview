package com.parkview.parkview.rest

interface RestHandler {
    fun handlePost(json: String)
    fun handleGet(json: String)
}