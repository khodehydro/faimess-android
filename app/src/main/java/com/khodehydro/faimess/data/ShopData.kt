package com.khodehydro.faimess.data

import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.*

/** Shop products — matches src/data/shop.ts */
val SHOP_CATEGORIES = listOf("all", "apparel", "accessories", "collectibles")

val SHOP_PRODUCTS = listOf(
    ShopProduct("hoodie-onstage", "On Stage hoodie", "apparel", 1_890_000, wasPrice = 2_350_000, badge = "bestseller", photoRes = R.drawable.brand_logo),
    ShopProduct("sneakers-lilac", "Lilac court sneakers", "apparel", 3_240_000, badge = "new", photoRes = R.drawable.brand_logo),
    ShopProduct("bracelet-star", "Star charm bracelet", "accessories", 640_000, photoRes = R.drawable.brand_logo),
    ShopProduct("photocards", "Photocard set", "collectibles", 380_000, badge = "bestseller", photoRes = R.drawable.brand_logo),
    ShopProduct("cap-midnight", "Midnight cap", "apparel", 720_000, photoRes = R.drawable.brand_logo),
    ShopProduct("tote-canvas", "Canvas tote", "accessories", 540_000, wasPrice = 680_000, photoRes = R.drawable.brand_logo),
    ShopProduct("lightstick-heart", "Heart lightstick", "collectibles", 1_450_000, badge = "new", photoRes = R.drawable.brand_logo),
    ShopProduct("tee-lavender", "Lavender tee", "apparel", 780_000, badge = "low", photoRes = R.drawable.brand_logo),
    ShopProduct("bomber-black", "Satin bomber", "apparel", 2_980_000, photoRes = R.drawable.brand_logo),
    ShopProduct("buckethat-lilac", "Lilac bucket hat", "apparel", 690_000, photoRes = R.drawable.brand_logo),
    ShopProduct("socks-star", "Star crew socks", "apparel", 260_000, photoRes = R.drawable.brand_logo),
    ShopProduct("backpack-tour", "Tour backpack", "accessories", 1_680_000, badge = "bestseller", photoRes = R.drawable.brand_logo),
    ShopProduct("phonecase-lilac", "Photocard phone case", "accessories", 420_000, badge = "new", photoRes = R.drawable.brand_logo),
    ShopProduct("keyring-star", "Acrylic keyring", "accessories", 190_000, photoRes = R.drawable.brand_logo),
    ShopProduct("earrings-crystal", "Crystal hoop earrings", "accessories", 480_000, photoRes = R.drawable.brand_logo),
    ShopProduct("poster-set", "Poster set · 3 prints", "collectibles", 460_000, photoRes = R.drawable.brand_logo),
    ShopProduct("stickers-pack", "Sticker pack · 24 pcs", "collectibles", 150_000, photoRes = R.drawable.brand_logo),
    ShopProduct("mug-lilac", "Lilac mug", "collectibles", 350_000, badge = "low", photoRes = R.drawable.brand_logo),
)

/** Badges — matches src/data/allBadges.ts (100 badges, sampling key ones) */
val ALL_BADGES = listOf(
    PlatformBadge("badge-first-listen", "اولین گوش", "First Listen", "اولین آهنگ خود را پخش کنید", "Play your first track", "listening", "common", 0, true, "2025-01-15"),
    PlatformBadge("badge-100-plays", "صد شنونده", "Century Listener", "۱۰۰ بار آهنگ پخش کنید", "Play 100 tracks", "listening", "common", 50, true, "2025-02-10"),
    PlatformBadge("badge-1000-plays", "هزار شنونده", "Millennium Ear", "۱۰۰۰ بار آهنگ پخش کنید", "Play 1,000 tracks", "listening", "rare", 500),
    PlatformBadge("badge-first-comment", "اولین نظر", "First Voice", "اولین نظر خود را بنویسید", "Post your first comment", "social", "common", 0, true, "2025-01-16"),
    PlatformBadge("badge-10-comments", "منتقد نوپا", "Rising Critic", "۱۰ نظر بنویسید", "Post 10 comments", "social", "common", 25, true, "2025-02-01"),
    PlatformBadge("badge-50-comments", "صدای جامعه", "Community Voice", "۵۰ نظر بنویسید", "Post 50 comments", "social", "rare", 250),
    PlatformBadge("badge-first-lyric", "مترجم لیریک", "Lyric Translator", "اولین لیریک ترجمه کنید", "Submit your first lyric translation", "lyrics", "rare", 120, true, "2025-03-01"),
    PlatformBadge("badge-5-lyrics", "استاد لیریک", "Lyric Master", "۵ لیریک ترجمه کنید", "Submit 5 lyric translations", "lyrics", "epic", 600),
    PlatformBadge("badge-first-follow", "اولین دنبال‌کننده", "First Follow", "یک آرتیست را دنبال کنید", "Follow your first artist", "social", "common", 0, true, "2025-01-15"),
    PlatformBadge("badge-7-day-streak", "یک هفته وفادار", "Week Warrior", "۷ روز متوالی گوش دهید", "Listen 7 days in a row", "loyalty", "rare", 175),
    PlatformBadge("badge-30-day-streak", "یک ماه عشق", "Monthly Devotion", "۳۰ روز متوالی گوش دهید", "Listen 30 days in a row", "loyalty", "epic", 750),
    PlatformBadge("badge-first-playlist", "سازنده پلی‌لیست", "Playlist Creator", "اولین پلی‌لیست بسازید", "Create your first playlist", "social", "common", 10),
    PlatformBadge("badge-night-owl", "جغد شب", "Night Owl", "بعد از نیمه‌شب گوش دهید", "Listen after midnight", "listening", "common", 5),
    PlatformBadge("badge-brand-purple", "بنفش برند", "Brand Purple", "به سطح برند برسید", "Reach brand tier", "special", "legendary", 5000),
    PlatformBadge("badge-invite-3", "دعوت‌کننده", "Inviter", "۳ نفر دعوت کنید", "Invite 3 friends", "social", "rare", 150),
    PlatformBadge("badge-early-adopter", "کاربر اولیه", "Early Adopter", "از روز اول با FAIMESS", "Joined in the first month", "special", "epic", 0, true, "2025-01-15"),
)

/** Points rules — matches src/data/points.ts POINT_RULES */
val POINT_RULES = listOf(
    PointRule("listening", "گوش دادن", "Listening", 0.02, 0, 0.0),       // per minute
    PointRule("comments", "نظرات", "Comments", 0.25, 0, 0.0),            // per comment
    PointRule("invites", "دعوت‌ها", "Invites", 3.0, 0, 0.0),            // per friend joined
    PointRule("tenure", "عضویت", "Tenure", 0.5, 0, 0.0),                // per day
    PointRule("lyrics", "لیریک", "Lyrics", 120.0, 0, 0.0),              // per approved sheet
)

/** Tiers — matches src/data/tiers.ts */
val USER_TIERS = listOf(
    UserLevelInfo(1, "seed", "بذر", "Seed", 0, 100, 0f),
    UserLevelInfo(2, "sprout", "جوانه", "Sprout", 100, 300, 0f),
    UserLevelInfo(3, "bloom", "شکوفه", "Bloom", 300, 600, 0f),
    UserLevelInfo(4, "grove", "باغ", "Grove", 600, 1000, 0f),
    UserLevelInfo(5, "forest", "جنگل", "Forest", 1000, 2000, 0f),
    UserLevelInfo(6, "canopy", "سایبان", "Canopy", 2000, 3500, 0f),
    UserLevelInfo(7, "skyline", "افق", "Skyline", 3500, 5000, 0f),
    UserLevelInfo(8, "brand", "برند", "Brand", 5000, 10000, 0f),
)

fun tierForPoints(points: Int): UserLevelInfo {
    val base = USER_TIERS.last { points >= it.minPoints }
    val next = USER_TIERS.firstOrNull { it.level == base.level + 1 }
    val progress = if (next != null) {
        ((points - base.minPoints).toFloat() / (next.minPoints - base.minPoints)).coerceIn(0f, 1f)
    } else 1f
    return base.copy(progressPercent = progress)
}