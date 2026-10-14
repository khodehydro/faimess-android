package com.khodehydro.faimess

import androidx.compose.material3.SnackbarHostState
import com.khodehydro.faimess.ui.LatestSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LatestSnackbarTest {
    @Test fun `rapid feedback replaces old messages instead of building a queue`() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val host = SnackbarHostState()
            val feedback = LatestSnackbar(host, scope)
            repeat(100) { index ->
                feedback.show("Action $index")
                assertEquals("Action $index", host.currentSnackbarData?.visuals?.message)
            }
            host.currentSnackbarData?.dismiss()
            assertNull(host.currentSnackbarData)
        } finally {
            scope.cancel()
        }
    }

    @Test fun `disposing the UI scope clears pending feedback`() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val host = SnackbarHostState()
        val feedback = LatestSnackbar(host, scope)
        feedback.show("Saved")
        scope.cancel()
        assertNull(host.currentSnackbarData)
    }
}
