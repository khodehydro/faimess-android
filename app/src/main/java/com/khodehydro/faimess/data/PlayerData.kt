package com.khodehydro.faimess.data

import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.*

/** Tracks queue — matches src/data/player.ts */
val TRACK_QUEUE = listOf(
    AudioTrack("nt1", "Afterglow", "NOVAE", "Afterglow", 192, plays = 2_840_000, isSingle = false, photoRes = R.drawable.brand_logo),
    AudioTrack("nt2", "Midnight Seoul", "AXION", "Long Exposure", 208, plays = 5_600_000, isSingle = true, photoRes = R.drawable.brand_logo),
    AudioTrack("nt3", "Paper Heart", "SEORA", "Paper Boats", 178, plays = 1_200_000, photoRes = R.drawable.brand_logo),
    AudioTrack("nt4", "Neon Bloom", "LUNEX", "Slow Motion", 185, plays = 980_000, photoRes = R.drawable.brand_logo),
    AudioTrack("nt5", "Halo Drive", "PRISM9", "Velvet Static", 221, plays = 3_400_000, photoRes = R.drawable.brand_logo),
    AudioTrack("nt6", "Silver Hour", "HANEUL", "Tokyo Window", 242, plays = 1_100_000, photoRes = R.drawable.brand_logo),
    AudioTrack("nt7", "Afterimage", "NOVAE", "Afterglow", 204, plays = 450_000, isSingle = true, photoRes = R.drawable.brand_logo),
    AudioTrack("tr1", "Midnight Seoul", "AXION", "Long Exposure", 208, plays = 5_600_000, photoRes = R.drawable.brand_logo),
    AudioTrack("tr2", "Afterglow", "NOVAE", "Afterglow", 192, plays = 2_840_000, photoRes = R.drawable.brand_logo),
    AudioTrack("tr3", "Cherry Static", "PRISM9", "Velvet Static", 199, plays = 1_800_000, photoRes = R.drawable.brand_logo),
    AudioTrack("tr4", "Paper Heart", "SEORA", "Paper Boats", 178, plays = 1_200_000, photoRes = R.drawable.brand_logo),
    AudioTrack("tr5", "Gravity", "VELVET MOON", "Nightbloom", 216, plays = 900_000, photoRes = R.drawable.brand_logo),
    AudioTrack("tr6", "Blue Signal", "KAIROS", "Blue Hour", 182, plays = 750_000, photoRes = R.drawable.brand_logo),
)

/** Lyrics — matches src/data/lyrics.ts */
val LYRICS_DB = mapOf(
    "nt1" to listOf(
        LyricLine(0, "너의 눈빛이 날 감싸", "نگاه تو مرا در بر می‌گیرد"),
        LyricLine(4, "은빛 달 아래 춤춰", "زیر نقره‌ای ماه می‌رقصیم"),
        LyricLine(9, "Afterglow 속에 번져", "در Afterglow گسترش می‌یابد"),
        LyricLine(14, "우리 둘만의 빛을 따라", "به دنبال نور مخصوص خودمان"),
        LyricLine(18, "멈추지 마 이 순간", "این لحظه را متوقف نکن"),
        LyricLine(23, "별빛이 쏟아지는 밤", "شبی که نور ستاره‌ها می‌بارد"),
        LyricLine(28, "너와 나 영원히", "تو و من تا ابد"),
        LyricLine(33, "Afterglow 영원히", "Afterglow تا ابد"),
    ),
    "nt2" to listOf(
        LyricLine(0, "서울의 밤이 깊어가", "شب سئول عمیق‌تر می‌شود"),
        LyricLine(5, "네온 간판 아래 서서", "زیر تابلوهای نئون ایستاده"),
        LyricLine(10, "Midnight Seoul 나를 안아", "Midnight Seoul مرا در آغوش بگیر"),
        LyricLine(15, "도시의 심장이 뛰어", "قلب شهر می‌تپد"),
        LyricLine(20, "빛나는 이 거리를", "این خیابان درخشان را"),
        LyricLine(25, "너와 함께 걸어갈게", "با تو قدم خواهم زد"),
    ),
    "nt7" to listOf(
        LyricLine(0, "사라져가는 기억 속", "در خاطرات محو شونده"),
        LyricLine(5, "너의 잔상이 보여", "باقیمانده‌ات را می‌بینم"),
        LyricLine(11, "Afterimage 남아있는", "Afterimage باقی‌مانده"),
        LyricLine(16, "우리의 마지막 순간", "آخرین لحظه ما"),
    ),
)

/** Newest tracks for the feed */
val NEWEST_TRACKS = TRACK_QUEUE.filter { it.id.startsWith("nt") }

/** Trending tracks with fire counts */
data class TrendingTrack(val track: AudioTrack, val fires: Int, val delta: Int)

val TRENDING_TRACKS = listOf(
    TrendingTrack(TRACK_QUEUE[1], 18420, 128),   // Midnight Seoul
    TrendingTrack(TRACK_QUEUE[0], 15260, 96),    // Afterglow
    TrendingTrack(TRACK_QUEUE[9], 12840, 64),    // Cherry Static
    TrendingTrack(TRACK_QUEUE[2], 9740, 41),     // Paper Heart
    TrendingTrack(TRACK_QUEUE[11], 7310, 18),    // Gravity
    TrendingTrack(TRACK_QUEUE[12], 5120, 9),     // Blue Signal
)

/** Community users — matches src/api/socialApi.ts COMMUNITY_USERS_SEED */
data class CommunityUser(
    val username: String,
    val displayName: String,
    val displayNameFa: String,
    val bio: String,
    val avatarSeed: Int,
    val favoriteGenre: String,
    val joinedAt: String,
    val points: Int,
    val role: String,
    val playlists: List<UserPlaylist> = emptyList(),
)

val COMMUNITY_USERS = listOf(
    CommunityUser("sori", "Sori", "سوری", "K-pop lover & synthwave addict. Listener, curator and lyric enthusiast.", 0, "Electro Pop / K-pop", "2025-01-15", 1840, "Listener · Premium"),
    CommunityUser("yuna_music", "یونا (Yuna)", "یونا", "علاقه‌مند به کی‌پاپ، مترجم لیریک و عاشق هارمونی‌های آوازی.", 1, "Vocal Pop & OST", "2024-11-01", 4820, "Community Moderator",
        listOf(UserPlaylist("pl_yuna_vocal", "Yuna's Midnight Vocals", "yuna_music", trackIds = listOf("nt1","nt2","nt3")), UserPlaylist("pl_yuna_ballads", "Rainy Seoul Ballads", "yuna_music", trackIds = listOf("nt3","nt5")))),
    CommunityUser("taehyun_fan", "تهیون (Taehyun)", "تهیون", "مجموعه‌دار سینث‌ویو، عاشق کاست‌های قدیمی.", 2, "Synthwave / Retro Pop", "2024-12-10", 3150, "Curator",
        listOf(UserPlaylist("pl_taehyun_synth", "Cyberpunk Neon Beats", "taehyun_fan", trackIds = listOf("nt2","nt4","nt6")))),
    CommunityUser("jxnnie_glow", "جنی (Jennie Glow)", "جنی", "انرژی صحنه، بیت‌های پرتحرک کمبک‌ها.", 3, "Dance Pop & Hip-Hop", "2025-01-05", 5890, "VIP Listener",
        listOf(UserPlaylist("pl_jennie_stage", "Main Stage Energy", "jxnnie_glow", trackIds = listOf("nt1","nt4","nt5")))),
    CommunityUser("haru_beats", "هارو (Haru Beats)", "هارو", "آهنگساز، طراح صدا و شنونده حرفه‌ای.", 4, "R&B / Electro", "2025-01-20", 2450, "Listener · Premium"),
    CommunityUser("ari_beats", "آری (Ari)", "آری", "عاشق ملودی‌های ابری و لیریک‌های عمیق.", 5, "Indie Pop & Ballads", "2025-02-01", 1980, "Active Member"),
    CommunityUser("sora_sky", "سورا (Sora)", "سورا", "شنونده تخصصی موسیقی متن سریال‌ها.", 6, "K-Drama OST", "2025-02-14", 2740, "OST Specialist"),
    CommunityUser("minho_k", "مینهو (Minho)", "مینهو", "تحلیلگر چارت‌ها و تاریخچه پاپ مدرن.", 7, "Dance & Disco", "2025-02-18", 3410, "Chart Analyst"),
)