package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.model.*
import com.khodehydro.faimess.ui.SocialViewModel
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

/* ---- Badges & Points Screen ---- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesPointsScreen(vm: SocialViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Badges & Points") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Badges") })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Points") })
            Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Level") })
        }
        when (tabIndex) {
            0 -> BadgesList(state.badges)
            1 -> PointsBreakdown(state.pointsRules, state.tierInfo)
            2 -> LevelProgress(state.tierInfo)
        }
    }
}

@Composable
private fun BadgesList(badges: List<PlatformBadge>) {
    val categories = listOf("all", "listening", "social", "lyrics", "loyalty", "special")
    var filter by remember { mutableStateOf("all") }
    val filtered = if (filter == "all") badges else badges.filter { it.category == filter }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(selected = filter == cat, onClick = { filter = cat }, label = { Text(cat.replaceFirstChar { it.uppercase() }, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("${badges.count { it.unlocked }} / ${badges.size} unlocked", color = Color.Gray, fontSize = 13.sp)
        }
        items(filtered) { badge -> BadgeItem(badge) }
    }
}

@Composable
fun BadgeItem(badge: PlatformBadge) {
    val rarityColor = when (badge.rarity) { "legendary" -> Color(0xFFFFD700); "epic" -> Color(0xFFA855F7); "rare" -> Color(0xFF3B82F6); else -> Color(0xFF9CA3AF) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (badge.unlocked) Color.White else Color(0xFFF9FAFB)),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(if (badge.unlocked) rarityColor.copy(alpha = 0.15f) else Subtle), contentAlignment = Alignment.Center) {
                Text(if (badge.unlocked) "🏆" else "🔒", fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(badge.nameFa, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = if (badge.unlocked) Ink else Ink.copy(alpha = 0.5f))
                    Spacer(Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = rarityColor.copy(alpha = 0.15f)) {
                        Text(badge.rarity.uppercase(), Modifier.padding(horizontal = 4.dp, vertical = 1.dp), fontSize = 9.sp, color = rarityColor, fontWeight = FontWeight.Bold)
                    }
                }
                Text(badge.descriptionFa, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${badge.requiredPoints} pts", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                if (badge.unlocked && badge.unlockedAt != null) { Text(badge.unlockedAt, fontSize = 10.sp, color = Color.Gray) }
            }
        }
    }
}

@Composable
private fun PointsBreakdown(rules: List<PointRule>, tierInfo: UserLevelInfo) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Primary)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Points", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text("${rules.sumOf { it.subtotal }.toInt()}", fontWeight = FontWeight.Bold, fontSize = 36.sp, color = Color.White)
                    Text("Tier: ${tierInfo.tierNameFa} (${tierInfo.tierNameEn})", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }
        item { Text("How you earn points", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink) }
        items(rules) { rule ->
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(rule.labelFa, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink)
                        Text("${rule.labelEn} · ${rule.value} per unit", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${rule.count}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)
                        Text("${"%.1f".format(rule.subtotal)} pts", fontSize = 12.sp, color = Primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelProgress(tier: UserLevelInfo) {
    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(120.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Level", fontSize = 12.sp, color = Color.Gray)
                Text("${tier.level}", fontWeight = FontWeight.Bold, fontSize = 36.sp, color = Primary)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(tier.tierNameFa, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink)
        Text(tier.tierNameEn, color = Color.Gray, fontSize = 16.sp)
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(progress = { tier.progressPercent }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)), color = Primary, trackColor = Subtle)
        Spacer(Modifier.height(8.dp))
        Text("${tier.minPoints} → ${tier.nextLevelPoints} points", fontSize = 13.sp, color = Color.Gray)
        Text("${"%.0f".format(tier.progressPercent * 100)}% to next level", fontSize = 13.sp, color = Primary)
    }
}

/* ---- Leaderboard Screen ---- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(vm: SocialViewModel, onBack: () -> Unit, onSelectUser: (String) -> Unit, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Leaderboard") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.leaderboard) { entry ->
                Card(Modifier.fillMaxWidth().clickable { onSelectUser(entry.username) }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("#${entry.rank}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = when (entry.rank) { 1 -> Color(0xFFFFD700); 2 -> Color(0xFFC0C0C0); 3 -> Color(0xFFCD7F32); else -> Color.Gray }, modifier = Modifier.width(36.dp))
                        Box(Modifier.size(40.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Text(entry.displayName.take(1), fontWeight = FontWeight.Bold, color = Primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(entry.displayName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink)
                            Text(entry.tier, fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${entry.points}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
                            Text("points", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

/* ---- Lyrics Submission Screen ---- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsSubmissionScreen(vm: SocialViewModel, trackId: String, trackTitle: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()
    var original by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Submit Lyrics") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Track: $trackTitle", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text("Submit Korean lyrics and your Persian translation. You'll earn 120 points if approved!", color = Color.Gray, fontSize = 13.sp)
            }
            item {
                OutlinedTextField(value = original, onValueChange = { original = it }, label = { Text("Original (Korean)") },
                    modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(12.dp), maxLines = 8)
            }
            item {
                OutlinedTextField(value = translation, onValueChange = { translation = it }, label = { Text("Persian Translation") },
                    modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(12.dp), maxLines = 8)
            }
            item {
                Button(onClick = { vm.submitLyrics(trackId, original, translation); submitted = true },
                    enabled = original.isNotBlank() && translation.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Submit Lyrics", fontSize = 16.sp)
                }
            }
            if (submitted) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))) {
                        Text("✅ Submitted! Your lyrics are pending review. +50 points for submitting.", Modifier.padding(16.dp), color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            // Previous submissions
            val mySubs = state.lyricSubmissions.filter { it.trackId == trackId }
            if (mySubs.isNotEmpty()) {
                item { Text("Your Submissions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink) }
                items(mySubs) { sub ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sub.trackTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink)
                                Spacer(Modifier.weight(1f))
                                Surface(shape = RoundedCornerShape(6.dp), color = when (sub.status) { "approved" -> Color(0xFF10B981); "rejected" -> Color(0xFFEF4444); else -> Color(0xFFF59E0B) }) {
                                    Text(sub.status.uppercase(), Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("${sub.lines} lines · ${sub.language} · ${sub.sentAt}", fontSize = 12.sp, color = Color.Gray)
                            Text("+${sub.points} points", fontSize = 12.sp, color = Primary)
                        }
                    }
                }
            }
        }
    }
}

/* ---- User Profile Screen ---- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(vm: SocialViewModel, onBack: () -> Unit, onNavigateToBadges: () -> Unit, onNavigateToLeaderboard: () -> Unit, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()
    val profile = state.viewedProfile ?: state.currentProfile ?: return

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Box(Modifier.fillMaxWidth().height(200.dp).background(Primary.copy(alpha = 0.12f))) {
                IconButton(onClick = onBack, Modifier.padding(12.dp).statusBarsPadding()) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        }
        item {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp).offset(y = (-36).dp).clip(CircleShape).background(Primary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Text(profile.displayName.take(1), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(profile.displayName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink)
                        Text(profile.handle, fontSize = 14.sp, color = Color.Gray)
                    }
                    if (!profile.isSelf) {
                        Button(onClick = { vm.toggleFollow(profile.username) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (profile.isFollowing) Subtle else Primary, contentColor = if (profile.isFollowing) Ink else Color.White),
                            shape = RoundedCornerShape(20.dp)) { Text(if (profile.isFollowing) "Following" else "Follow") }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(profile.bio, fontSize = 14.sp, color = Ink.copy(alpha = 0.8f))
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column { Text("${profile.followersCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink); Text("Followers", fontSize = 12.sp, color = Color.Gray) }
                    Column { Text("${profile.followingCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink); Text("Following", fontSize = 12.sp, color = Color.Gray) }
                    Column { Text("${profile.points}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary); Text("Points", fontSize = 12.sp, color = Color.Gray) }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(Modifier.weight(1f).clickable(onClick = onNavigateToBadges), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Subtle)) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆", fontSize = 24.sp); Text("Badges", fontSize = 12.sp, color = Ink)
                        }
                    }
                    Card(Modifier.weight(1f).clickable(onClick = onNavigateToLeaderboard), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Subtle)) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🥇", fontSize = 24.sp); Text("Rankings", fontSize = 12.sp, color = Ink)
                        }
                    }
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Subtle)) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎵", fontSize = 24.sp); Text("Tier: ${profile.tier}", fontSize = 12.sp, color = Ink)
                        }
                    }
                }
            }
        }
        // User playlists
        if (profile.playlists.isNotEmpty()) {
            item {
                Text("Playlists", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            }
            items(profile.playlists) { pl ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Text("🎵", fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(pl.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink)
                            Text("${pl.trackIds.size} tracks", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}