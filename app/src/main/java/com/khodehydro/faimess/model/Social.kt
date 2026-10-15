package com.khodehydro.faimess.model

/* ---- User Profile ---- */

data class UserProfile(
    val username: String,
    val displayName: String,
    val handle: String,
    val bio: String,
    val avatarSeed: Int = 0,
    val bannerSeed: Int = 0,
    val favoriteGenre: String,
    val joinedAt: String,
    val points: Int,
    val role: String,
    val tier: String,
    val isSelf: Boolean = false,
    val isFollowing: Boolean = false,
    val anthemTrackId: String? = null,
    val biasArtistId: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val referralCode: String? = null,
    val playlists: List<UserPlaylist> = emptyList(),
)

data class FollowStats(
    val followersCount: Int,
    val followingCount: Int,
)

/* ---- Badges ---- */

data class PlatformBadge(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val descriptionFa: String,
    val descriptionEn: String,
    val category: String,       // "listening" | "social" | "lyrics" | "loyalty" | "special"
    val rarity: String,         // "common" | "rare" | "epic" | "legendary"
    val requiredPoints: Int,
    val unlocked: Boolean = false,
    val unlockedAt: String? = null,
    val iconSeed: Int = 0,
)

data class CommentBadge(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val iconSeed: Int,
    val requiredPoints: Int,
)

data class BadgeProgress(
    val badge: PlatformBadge,
    val progressPercent: Float,
    val remainingPoints: Int,
)

/* ---- Points ---- */

data class PointRule(
    val id: String,             // "listening" | "comments" | "invites" | "tenure" | "lyrics"
    val labelFa: String,
    val labelEn: String,
    val value: Double,          // points per unit
    val count: Int,
    val subtotal: Double,
)

data class FanActivity(
    val listeningMinutes: Int = 0,
    val comments: Int = 0,
    val invites: Int = 0,
    val days: Int = 0,
    val lyricSheets: Int = 0,
)

/* ---- Level / Tier ---- */

data class UserLevelInfo(
    val level: Int,
    val tierKey: String,
    val tierNameFa: String,
    val tierNameEn: String,
    val minPoints: Int,
    val nextLevelPoints: Int,
    val progressPercent: Float,
)

/* ---- Lyrics Submission ---- */

data class LyricSubmission(
    val id: String,
    val trackId: String,
    val trackTitle: String,
    val lines: Int,
    val status: String,         // "pending" | "approved" | "rejected"
    val sentAt: String,
    val points: Int,
    val language: String,
    val original: String,
    val translation: String,
)

/* ---- Referral ---- */

data class ReferralInfo(
    val inviteCode: String,
    val inviteLink: String,
    val rewardPerInvite: Int,
    val invitedCount: Int,
    val totalPointsEarned: Int,
    val invitedUsers: List<InvitedUser>,
)

data class InvitedUser(
    val username: String,
    val displayName: String,
    val joinedAt: String,
    val earnedPoints: Int,
    val avatarSeed: Int = 0,
)

/* ---- Banner ---- */

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val gradientStart: Long,
    val gradientEnd: Long,
    val seed: Int,
)

/* ---- Report ---- */

enum class ReportReason { SPAM, HARASSMENT, COPYRIGHT, MISINFORMATION, OTHER }

data class ReportDto(
    val targetType: String,
    val targetId: String,
    val reason: ReportReason,
    val details: String = "",
)