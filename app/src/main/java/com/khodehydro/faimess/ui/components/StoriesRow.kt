package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.Story
import com.khodehydro.faimess.ui.theme.Flame
import com.khodehydro.faimess.ui.theme.Heart
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep
import com.khodehydro.faimess.ui.theme.Teal

@Composable
fun StoriesRow(stories: List<Story>, onStory: (Story) -> Unit, onAddStory: () -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().background(Color.White).testTag("stories"),
        contentPadding = PaddingValues(start = 11.dp, end = 11.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(stories, key = { it.person.id }) { story ->
            StoryItem(story, onClick = { if (story.isSelf) onAddStory() else onStory(story) })
        }
    }
}

@Composable
private fun StoryItem(story: Story, onClick: () -> Unit) {
    val description = if (story.isSelf) stringResource(R.string.add_story)
    else stringResource(R.string.story_description, story.person.name)
    // The site's story rail: an unseen ring runs primary-deep → primary → teal; LIVE stays warm.
    val ring = if (story.isLive) listOf(Flame, Heart, Flame) else listOf(PrimaryDeep, Primary, Teal)

    Column(
        modifier = Modifier.width(64.dp).clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }.testTag("story_${story.person.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(Modifier.size(62.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(57.dp).border(1.8.dp, Brush.linearGradient(ring), CircleShape).padding(3.5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Avatar(story.person, 50.dp)
            }
            if (story.isSelf) {
                Box(
                    Modifier.align(Alignment.BottomEnd).padding(end = 1.dp, bottom = 2.dp)
                        .size(19.dp).background(Color.White, CircleShape)
                        .border(1.dp, Line, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(R.drawable.ic_plus, modifier = Modifier.size(12.dp), tint = PrimaryDeep)
                }
            }
            if (story.isLive) {
                Text(
                    stringResource(R.string.live),
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .background(Heart, RoundedCornerShape(4.dp)).padding(horizontal = 7.dp, vertical = 1.dp),
                    color = Color.White,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 9.sp,
                )
            }
        }
        Text(
            if (story.isSelf) stringResource(R.string.me) else story.person.name,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            color = Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
