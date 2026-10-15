package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.PostCard
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep

@Composable
fun ProfileScreen(
    posts: List<FeedPost>,
    interactions: PostInteractions,
    modifier: Modifier = Modifier,
    onBadges: () -> Unit = {},
    onLeaderboard: () -> Unit = {},
    onMyProfile: () -> Unit = {},
) {
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
                    Text(stringResource(R.string.demo_profile), style = MaterialTheme.typography.labelMedium, color = PrimaryDeep)
                    Text(stringResource(R.string.profile_bio), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = InkMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(stringResource(R.string.profile_post_count, myPosts.size), style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(R.string.profile_saved_count, saved.size), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        // Quick-access links to new features
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfileLink("🏆", "Badges", Modifier.weight(1f), onBadges)
                ProfileLink("🥇", "Rankings", Modifier.weight(1f), onLeaderboard)
                ProfileLink("👤", "Profile", Modifier.weight(1f), onMyProfile)
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

@Composable
private fun ProfileLink(icon: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
    ) {
        Column(Modifier.padding(vertical = 14.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
        }
    }
}