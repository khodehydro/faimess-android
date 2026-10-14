package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.PostCard
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.theme.Leaf
import com.khodehydro.faimess.ui.theme.MutedInk

@Composable
fun ProfileScreen(posts: List<FeedPost>, interactions: PostInteractions, modifier: Modifier = Modifier) {
    var savedOnly by rememberSaveable { mutableStateOf(false) }
    val myPosts = remember(posts) { posts.filter { it.author.id == DemoContent.me.id } }
    val saved = remember(posts) { posts.filter { it.isSaved } }
    val visible = if (savedOnly) saved else myPosts
    LazyColumn(
        modifier = modifier.testTag("profile_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 124.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Surface(shape = RoundedCornerShape(28.dp), color = Color.White) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Avatar(DemoContent.me, 76.dp)
                    Text(DemoContent.me.name, style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.demo_profile), style = MaterialTheme.typography.labelMedium, color = Leaf)
                    Text(stringResource(R.string.profile_bio), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MutedInk)
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(stringResource(R.string.profile_post_count, myPosts.size), style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(R.string.profile_saved_count, saved.size), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !savedOnly, onClick = { savedOnly = false }, label = { Text(stringResource(R.string.my_posts)) })
                FilterChip(selected = savedOnly, onClick = { savedOnly = true }, label = { Text(stringResource(R.string.saved)) })
            }
        }
        if (visible.isEmpty()) {
            item {
                EmptyState(R.drawable.ic_bookmark, stringResource(R.string.no_saved), stringResource(R.string.no_saved_description))
            }
        }
        items(visible, key = { it.id }, contentType = { "post" }) { post -> PostCard(post, interactions) }
    }
}
