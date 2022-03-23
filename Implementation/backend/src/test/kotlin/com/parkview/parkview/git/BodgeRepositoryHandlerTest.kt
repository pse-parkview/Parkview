package com.parkview.parkview.git

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BodgeRepositoryHandlerTest {
    val handler = BodgeRepositoryHandler("pse-parkview", "parkview-data")

    @Test
    fun test_branch_index_fetching() = runTest {
        var text: String? = null
        val url = "https://raw.githubusercontent.com/pse-parkview/parkview-data/main/git_data/branches"
        launch {
            text = window.fetch(url).await().text().await()
        }.join()
        assertNotNull(text)
    }

    @Test
    fun test_shit() = runTest {
        val branches = handler.getAvailableBranches()
        assertTrue(branches.isNotEmpty())
    }
}