package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.LocalBottomChromeHeight
import com.khodehydro.faimess.ui.theme.InkMuted

@Composable
fun CalendarScreen(onDiscover: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(18.dp).padding(bottom = LocalBottomChromeHeight.current)
            .testTag("calendar_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.calendar_title), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.calendar_subtitle), style = MaterialTheme.typography.bodyMedium, color = InkMuted)
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = Color.White) {
            EmptyState(
                R.drawable.ic_calendar_outline,
                stringResource(R.string.no_plans),
                stringResource(R.string.no_plans_description),
                action = {
                    Button(onClick = onDiscover) { Text(stringResource(R.string.find_inspiration)) }
                },
            )
        }
    }
}
