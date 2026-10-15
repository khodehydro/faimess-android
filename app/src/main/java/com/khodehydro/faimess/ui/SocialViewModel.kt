package com.khodehydro.faimess.ui

import androidx.lifecycle.ViewModel
import com.khodehydro.faimess.data.*
import com.khodehydro.faimess.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/* ------------------------------------------------------------------ *
 *  Social ViewModel — Profiles, Follows, Badges, Points, Leaderboard
 * ------------------------------------------------------------------ */

data class SocialUiState(
    val currentProfile: UserProfile? = null,
    val viewedProfile: UserProfile? = null,
    val badges: List<PlatformBadge> = ALL_BADGES,
    val pointsRules: List<PointRule> = POINT_RULES,
    val tierInfo: UserLevelInfo = tierForPoints(1840),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val isFollowing: Boolean = false,
    val followStats: FollowStats = FollowStats(0, 0),
    val lyricSubmissions: List<LyricSubmission> = emptyList(),
    val referralInfo: ReferralInfo? = null,
    val comments: List<TrackComment> = emptyList(),
    val showReportDialog: Boolean = false,
    val reportTarget: String = "",
)

class SocialViewModel : ViewModel() {
    private val _state = MutableStateFlow(SocialUiState())
    val state: StateFlow<SocialUiState> = _state.asStateFlow()

    init {
        loadLeaderboard()
        loadCurrentUser()
        loadReferralInfo()
    }

    private fun loadCurrentUser() {
        val user = UserProfile(
            username = "sori", displayName = "Sori", handle = "@sori",
            bio = "K-pop lover & synthwave addict. Listener, curator and lyric enthusiast.",
            favoriteGenre = "Electro Pop / K-pop", joinedAt = "2025-01-15",
            points = 1840, role = "Listener · Premium", tier = "Forest",
            isSelf = true, followersCount = 42, followingCount = 8,
            anthemTrackId = "nt1", biasArtistId = "ar-kairos", referralCode = "FM-SORI-123",
        )
        _state.update { it.copy(currentProfile = user, tierInfo = tierForPoints(1840)) }
    }

    private fun loadLeaderboard() {
        val entries = COMMUNITY_USERS.sortedByDescending { it.points }.mapIndexed { index, u ->
            LeaderboardEntry(index + 1, u.username, u.displayName, u.avatarSeed, u.points, tierForPoints(u.points).tierNameEn, ALL_BADGES.count { b -> b.unlocked })
        }
        _state.update { it.copy(leaderboard = entries) }
    }

    private fun loadReferralInfo() {
        _state.update {
            it.copy(referralInfo = ReferralInfo("FM-SORI-123", "https://faimess.app/invite/FM-SORI-123", 3, 6, 18,
                listOf(
                    InvitedUser("taehyun_fan", "تهیون", "2025-01-18", 3, 2),
                    InvitedUser("kairos_orbit", "کایروس", "2025-01-22", 3, 4),
                    InvitedUser("miso_melody", "میسو", "2025-01-28", 3, 9),
                )))
        }
    }

    fun viewProfile(username: String) {
        val community = COMMUNITY_USERS.find { it.username == username }
        if (community != null) {
            val profile = UserProfile(
                username = community.username, displayName = community.displayName,
                handle = "@${community.username}", bio = community.bio,
                avatarSeed = community.avatarSeed, favoriteGenre = community.favoriteGenre,
                joinedAt = community.joinedAt, points = community.points, role = community.role,
                tier = tierForPoints(community.points).tierNameEn,
                isSelf = community.username == "sori", isFollowing = true,
                followersCount = (community.points / 10), followingCount = 8,
                playlists = community.playlists,
            )
            _state.update { it.copy(viewedProfile = profile) }
        }
    }

    fun clearViewedProfile() = _state.update { it.copy(viewedProfile = null) }

    fun toggleFollow(username: String) {
        _state.update { state ->
            val updated = if (state.viewedProfile?.username == username) {
                state.viewedProfile?.copy(isFollowing = !state.viewedProfile!!.isFollowing)
            } else null
            state.copy(viewedProfile = updated, isFollowing = updated?.isFollowing ?: state.isFollowing)
        }
    }

    fun loadComments(targetId: String) {
        // Load from news data or track comments
        val newsComments = ALL_NEWS.find { it.id == targetId }?.comments ?: emptyList()
        val trackComments = newsComments.map { nc ->
            TrackComment(nc.id, "news", targetId, targetId, nc.author, nc.handle, nc.avatarSeed, nc.text, likes = nc.likes, liked = nc.liked,
                replies = nc.replies.map { r -> TrackComment(r.id, "news", targetId, targetId, r.author, r.handle, r.avatarSeed, r.text, likes = r.likes) })
        }
        _state.update { it.copy(comments = trackComments) }
    }

    fun addComment(targetId: String, text: String) {
        val user = _state.value.currentProfile ?: return
        val comment = TrackComment("cm-${System.currentTimeMillis()}", "track", targetId, targetId, user.displayName, user.handle, user.avatarSeed, text)
        _state.update { it.copy(comments = listOf(comment) + it.comments) }
    }

    fun toggleLikeComment(commentId: String) {
        _state.update { state ->
            state.copy(comments = state.comments.map { c ->
                if (c.id == commentId) c.copy(liked = !c.liked, likes = if (c.liked) c.likes - 1 else c.likes + 1) else c
            })
        }
    }

    fun showReport(targetId: String) = _state.update { it.copy(showReportDialog = true, reportTarget = targetId) }
    fun dismissReport() = _state.update { it.copy(showReportDialog = false, reportTarget = "") }

    fun submitReport(reason: ReportReason, details: String) {
        // In production, call API. For now, just dismiss.
        _state.update { it.copy(showReportDialog = false, reportTarget = "") }
    }

    fun submitLyrics(trackId: String, original: String, translation: String) {
        val submission = LyricSubmission(
            id = "sub-${System.currentTimeMillis()}", trackId = trackId, trackTitle = TRACK_QUEUE.find { it.id == trackId }?.title ?: "Track",
            lines = original.lines().size, status = "pending", sentAt = "Just now", points = 50, language = "Korean / Persian",
            original = original, translation = translation,
        )
        _state.update { it.copy(lyricSubmissions = listOf(submission) + it.lyricSubmissions) }
    }
}