package com.khodehydro.faimess.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Feedback describes the latest completed action, not a queue of obsolete saves/shares.
 * Owned by the composition's scope, so pending work is cancelled when the app UI is disposed.
 * This never delays or debounces the underlying action.
 */
internal class LatestSnackbar(
    private val host: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    private var pending: Job? = null

    fun show(message: String) {
        pending?.cancel()
        host.currentSnackbarData?.dismiss()
        pending = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            host.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
}
