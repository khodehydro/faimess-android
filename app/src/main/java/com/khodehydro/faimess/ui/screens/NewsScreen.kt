package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.model.NewsComment
import com.khodehydro.faimess.model.NewsItem
import com.khodehydro.faimess.ui.NewsShopViewModel
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

@Composable
fun NewsScreen(vm: NewsShopViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()

    if (state.selectedNews != null) {
        NewsDetailScreen(
            item = state.selectedNews!!,
            onBack = vm::clearNewsSelection,
            onLike = { vm.likeNews(state.selectedNews!!.id) },
            onComment = { text -> vm.addNewsComment(state.selectedNews!!.id, text) },
            modifier = modifier,
        )
    } else {
        NewsListScreen(items = state.news, filter = state.newsFilter, onFilter = vm::filterNews, onSelect = vm::selectNews, modifier = modifier)
    }
}

@Composable
private fun NewsListScreen(items: List<NewsItem>, filter: String, onFilter: (String) -> Unit, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val categories = listOf("all", "Tour", "Charts", "Editorial", "Awards", "Comeback")
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("News", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(selected = filter == cat, onClick = { onFilter(cat) }, label = { Text(cat, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White))
                }
            }
        }
        items(items) { news -> NewsCard(news, onClick = { onSelect(news.id) }) }
    }
}

@Composable
fun NewsCard(item: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(item.tag, fontSize = 11.sp) }, shape = RoundedCornerShape(8.dp),
                    colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.1f), labelColor = Primary))
                Spacer(Modifier.width(8.dp))
                Text(item.source, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text("· ${item.ago}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(item.excerpt, fontSize = 13.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("♥ ${formatCount(item.likes)}", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Text("💬 ${item.commentsCount}", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Text("👁 ${formatCount(item.views)}", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.weight(1f))
                Text("by ${item.author.name}", fontSize = 12.sp, color = Primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsDetailScreen(item: NewsItem, onBack: () -> Unit, onLike: () -> Unit, onComment: (String) -> Unit, modifier: Modifier = Modifier) {
    var commentText by remember { mutableStateOf("") }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            TopAppBar(title = { Text("News") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                AssistChip(onClick = {}, label = { Text(item.tag) }, shape = RoundedCornerShape(8.dp),
                    colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.1f), labelColor = Primary))
                Spacer(Modifier.height(8.dp))
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(Subtle), contentAlignment = Alignment.Center) {
                        Text(item.author.name.take(1), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(item.author.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink)
                        Text("${item.author.role} · ${item.ago}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(item.excerpt, fontSize = 15.sp, lineHeight = 24.sp, color = Ink)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    TextButton(onClick = onLike) { Text("♥ ${formatCount(item.likes)}") }
                    Text("👁 ${formatCount(item.views)} views", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }
        // Comments
        item {
            Column(Modifier.padding(16.dp)) {
                HorizontalDivider(color = Subtle)
                Spacer(Modifier.height(12.dp))
                Text("Comments (${item.commentsCount})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = commentText, onValueChange = { commentText = it }, placeholder = { Text("Write a comment…") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(8.dp))
                Button(onClick = { if (commentText.isNotBlank()) { onComment(commentText); commentText = "" } },
                    enabled = commentText.isNotBlank(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Post Comment")
                }
            }
        }
        items(item.comments) { comment -> CommentItem(comment, depth = 0) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun CommentItem(comment: NewsComment, depth: Int) {
    Column(Modifier.padding(start = (depth * 24).dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(28.dp).clip(CircleShape).background(Subtle), contentAlignment = Alignment.Center) {
                Text(comment.author.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            Spacer(Modifier.width(8.dp))
            Text(comment.author, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Ink)
            Spacer(Modifier.width(4.dp))
            Text(comment.handle, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.width(4.dp))
            Text("· ${comment.time}", fontSize = 11.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(4.dp))
        Text(comment.text, fontSize = 14.sp, color = Ink, modifier = Modifier.padding(start = 36.dp))
        Spacer(Modifier.height(2.dp))
        Row(Modifier.padding(start = 36.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("♥ ${comment.likes}", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.width(12.dp))
            Text("Reply", fontSize = 12.sp, color = Primary, modifier = Modifier.clickable { })
        }
        comment.replies.forEach { reply -> CommentItem(reply, depth = depth + 1) }
    }
}

private fun formatCount(n: Int): String = when {
    n >= 1000 -> "${"%.1f".format(n / 1000.0)}K"
    else -> n.toString()
}