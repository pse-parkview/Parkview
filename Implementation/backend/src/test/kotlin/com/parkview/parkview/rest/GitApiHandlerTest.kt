package com.parkview.parkview.rest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GitApiHandlerTest {
    @Test
    fun `test call to test repo`() {
        val apiHandler = GitApiHandler("Hello-World", "octocat")
        val branch = apiHandler.fetchGitHistory("octocat-patch-1", 1)

        assert(branch[0].sha == "b1b3f9723831141a31a1a7252a213e216ea76e56") // check first commit
        assert(branch[3].sha == "553c2077f0edc3d5dc5d17262f6aa498e69d6f8e") // check first commit
    }
}