package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.Story
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Lime

@Composable
fun StoryViewer(stories: List<Story>, initialPersonId: String, onDismiss: () -> Unit) {
    if (stories.isEmpty()) return
    var index by rememberSaveable(initialPersonId) {
        mutableIntStateOf(stories.indexOfFirst { it.person.id == initialPersonId }.coerceAtLeast(0))
    }
    val story = stories[index.coerceIn(stories.indices)]
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Ink).systemBarsPadding().padding(14.dp)
                .testTag("story_viewer"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                stories.forEachIndexed { segment, _ ->
                    Box(Modifier.weight(1f).height(3.dp).background(
                        if (segment <= index) Lime else Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp),
                    ))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Avatar(story.person, 36.dp)
                Column(Modifier.weight(1f)) {
                    Text(story.person.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(if (story.isLive) R.string.demo_live_note else R.string.demo_story),
                        color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    AppIcon(R.drawable.ic_close, stringResource(R.string.close), tint = Color.White)
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(24.dp))) {
                OfflinePhoto(story.image, story.caption, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                Text(
                    story.caption,
                    modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { index-- }, enabled = index > 0, modifier = Modifier.size(48.dp)) {
                    AppIcon(R.drawable.ic_arrow_back, stringResource(R.string.previous_story), tint = if (index > 0) Color.White else Color.White.copy(alpha = 0.3f))
                }
                Spacer(Modifier.weight(1f))
                Text("${index + 1} / ${stories.size}", color = Color.White, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { if (index < stories.lastIndex) index++ else onDismiss() }, modifier = Modifier.size(48.dp)) {
                    AppIcon(R.drawable.ic_arrow_forward, stringResource(R.string.next_story), tint = Color.White)
                }
            }
        }
    }
}
