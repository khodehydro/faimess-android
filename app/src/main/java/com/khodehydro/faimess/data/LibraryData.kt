package com.khodehydro.faimess.data

import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.*

/** The FAIMESS roster — artists, albums, playlists. Matches src/data/library.ts exactly. */

val ALL_ARTISTS = listOf(
    Artist("ar-novae", "NOVAE", "NV", "Boy group", "Electro pop", "4.8M monthly", "2.4M followers", following = true, newRelease = true, verified = true, seed = 0, photoRes = R.drawable.brand_logo),
    Artist("ar-seora", "SEORA", "SR", "Soloist", "Alt R&B", "3.1M monthly", "1.8M followers", following = true, newRelease = true, verified = true, seed = 2, photoRes = R.drawable.brand_logo),
    Artist("ar-axion", "AXION", "AX", "Boy group", "Hip-hop", "5.6M monthly", "3.2M followers", following = true, verified = true, seed = 4, photoRes = R.drawable.brand_logo),
    Artist("ar-lunex", "LUNEX", "LX", "Boy group", "Synth pop", "2.7M monthly", "1.5M followers", following = true, verified = true, seed = 1, photoRes = R.drawable.brand_logo),
    Artist("ar-prism9", "PRISM9", "P9", "Girl group", "Dance pop", "6.4M monthly", "4.1M followers", following = true, newRelease = true, verified = true, seed = 3, photoRes = R.drawable.brand_logo),
    Artist("ar-velvet-moon", "VELVET MOON", "VM", "Duo", "City pop", "1.4M monthly", "890K followers", following = true, seed = 7, photoRes = R.drawable.brand_logo),
    Artist("ar-haneul", "HANEUL", "HN", "Soloist", "Ballad", "2.2M monthly", "1.2M followers", following = true, verified = true, seed = 6, photoRes = R.drawable.brand_logo),
    Artist("ar-kairos", "KAIROS", "KR", "Boy group", "Alt R&B", "3.9M monthly", "2.1M followers", following = true, verified = true, seed = 5, photoRes = R.drawable.brand_logo),
)

val ALL_ALBUMS = listOf(
    Album("al-afterglow", "Afterglow", "NOVAE", 2025, 11, "Today", 0, photoRes = R.drawable.brand_logo),
    Album("al-blue-hour", "Blue Hour", "KAIROS", 2025, 9, "Yesterday", 1, photoRes = R.drawable.brand_logo),
    Album("al-velvet-static", "Velvet Static", "PRISM9", 2025, 12, "2 days ago", 2, photoRes = R.drawable.brand_logo),
    Album("al-nightbloom", "Nightbloom", "VELVET MOON", 2025, 8, "4 days ago", 4, photoRes = R.drawable.brand_logo),
    Album("al-tokyo-window", "Tokyo Window", "HANEUL", 2025, 13, "6 days ago", 6, photoRes = R.drawable.brand_logo),
    Album("al-slow-motion", "Slow Motion", "LUNEX", 2025, 7, "1 week ago", 5, photoRes = R.drawable.brand_logo),
    Album("al-paper-boats", "Paper Boats", "SEORA", 2024, 8, "3 weeks ago", 3, photoRes = R.drawable.brand_logo),
    Album("al-long-exposure", "Long Exposure", "AXION", 2024, 6, "2 months ago", 7, photoRes = R.drawable.brand_logo),
)

val CURATED_PLAYLISTS = listOf(
    LibraryPlaylist("pl-midnight-drive", "Midnight Seoul Drive", "Faimess", 48, "3h 12m", "Mellow", 0, photoRes = R.drawable.brand_logo),
    LibraryPlaylist("pl-comeback", "Comeback Countdown", "Faimess", 62, "4h 05m", "Hype", 1, photoRes = R.drawable.brand_logo),
    LibraryPlaylist("pl-golden-hour", "Golden Hour City Pop", "HANEUL", 27, "1h 44m", "Sunny", 2, photoRes = R.drawable.brand_logo),
    LibraryPlaylist("pl-rainy-window", "Rainy Window", "Faimess", 35, "2h 18m", "Soft", 3, photoRes = R.drawable.brand_logo),
    LibraryPlaylist("pl-deep-focus", "Deep Focus", "Faimess", 71, "5h 02m", "Ambient", 4, photoRes = R.drawable.brand_logo),
    LibraryPlaylist("pl-weekend-reset", "Weekend Reset", "PRISM9", 40, "2h 36m", "Warm", 5, photoRes = R.drawable.brand_logo),
)

/** Artist fandoms — matches src/api/socialApi.ts ARTIST_FANDOMS */
val ARTIST_FANDOMS = mapOf(
    "bts" to FandomInfo("bts", "BTS (بی‌تی‌اس)", "آرمی (ARMY)", "A.R.M.Y", "rgba(147,51,234,0.55)", "#9333ea", "جوانان ضد گلوله و اقیانوس بنفش آرمی"),
    "blackpink" to FandomInfo("blackpink", "BLACKPINK (بلک‌پینک)", "بلینک (BLINK)", "BLINK", "rgba(236,72,153,0.55)", "#ec4899", "بلک‌پینک در منطقه شما"),
    "stray-kids" to FandomInfo("stray-kids", "Stray Kids (استری کیدز)", "استی (STAY)", "STAY", "rgba(225,29,72,0.55)", "#e11d48", "جایی که استری کیدز بماند، استی هست"),
    "newjeans" to FandomInfo("newjeans", "NewJeans (نیوجینز)", "بانیز (Bunnies)", "Bunnies", "rgba(14,165,233,0.55)", "#0ea5e9", "همیشه تازه و خاطره‌انگیز"),
    "twice" to FandomInfo("twice", "TWICE (توایس)", "وانس (ONCE)", "ONCE", "rgba(249,115,22,0.55)", "#f97316", "یک بار احساس، دو بار عشق"),
    "aespa" to FandomInfo("aespa", "Aespa (اسپا)", "مای (MY)", "MY", "rgba(99,102,241,0.55)", "#6366f1", "ارتباط با دنیای مجازی و آواتارها"),
    "seventeen" to FandomInfo("seventeen", "SEVENTEEN (سونتین)", "کرات (CARAT)", "CARAT", "rgba(244,114,182,0.55)", "#f472b6", "درخشش الماس‌های رز کوارتز و سرنیتی"),
    "txt" to FandomInfo("txt", "TXT (تی‌اکس‌تی)", "موآ (MOA)", "MOA", "rgba(6,182,212,0.55)", "#06b6d4", "لحظات همبستگی ما تا همیشه"),
    "enhypen" to FandomInfo("enhypen", "ENHYPEN (انهایپن)", "انژین (ENGENE)", "ENGENE", "rgba(234,179,8,0.55)", "#eab308", "موتور محرک و اتصال بی‌پایان"),
    "le-sserafim" to FandomInfo("le-sserafim", "LE SSERAFIM (لسرافیم)", "فیرنات (FEARNOT)", "FEARNOT", "rgba(20,184,166,0.55)", "#14b8a6", "بی‌باک و استوار در مسیر رویاها"),
    "ive" to FandomInfo("ive", "IVE (آیو)", "دایو (DIVE)", "DIVE", "rgba(139,92,246,0.55)", "#8b5cf6", "شیرجه در اعتمادبه‌نفس و وقار"),
    "exo" to FandomInfo("exo", "EXO (اکسو)", "اکسوال (EXO-L)", "EXO-L", "rgba(202,138,4,0.55)", "#ca8a04", "ما یکی هستیم، تا همیشه اکسو"),
    "ar-kairos" to FandomInfo("ar-kairos", "KAIROS", "اوربیت (ORBIT)", "ORBIT", "rgba(130,103,240,0.45)", "#8267f0", "مدار بی‌پایان ستارگان"),
    "ar-prism9" to FandomInfo("ar-prism9", "PRISM9", "اسپکتروم (SPECTRUM)", "SPECTRUM", "rgba(236,72,153,0.45)", "#ec4899", "درخشش ۹ رنگ منشور"),
    "ar-novae" to FandomInfo("ar-novae", "NOVAE", "سوپرنوا (SUPERNOVA)", "SUPERNOVA", "rgba(59,130,246,0.45)", "#3b82f6", "انفجار نور و انرژی کیهانی"),
    "ar-seora" to FandomInfo("ar-seora", "SEORA", "سرافیم (SERAPHIM)", "SERAPHIM", "rgba(168,85,247,0.45)", "#a855f7", "نوای فرشتگان شب‌های سئول"),
    "ar-axion" to FandomInfo("ar-axion", "AXION", "پالس (PULSE)", "PULSE", "rgba(249,115,22,0.45)", "#f97316", "ضربان ریتم و خیابان"),
    "ar-lunex" to FandomInfo("ar-lunex", "LUNEX", "لونار (LUNAR)", "LUNAR", "rgba(6,182,212,0.45)", "#06b6d4", "مهتاب پاپ کره‌ای"),
    "ar-velvet-moon" to FandomInfo("ar-velvet-moon", "VELVET MOON", "ولوت (VELVET)", "VELVET", "rgba(225,29,72,0.45)", "#e11d48", "آرامش مخملین موسیقی شب"),
    "ar-haneul" to FandomInfo("ar-haneul", "HANEUL", "کلودز (CLOUDS)", "CLOUDS", "rgba(16,185,129,0.45)", "#10b981", "آسمان آبی احساس"),
)

/** Banners — matches src/data/banners.ts */
val HOME_BANNERS = listOf(
    Banner("bn1", "NOVAE World Tour 2025", "\"Afterglow\" — 12 cities, Seoul opener live", "Get Tickets", 0xFF6366F1, 0xFF8B5CF6, 0),
    Banner("bn2", "New Album: Blue Hour", "KAIROS returns with their boldest record yet", "Listen Now", 0xFF8267F0, 0xFFA855F7, 1),
    Banner("bn3", "FAIMESS Shop Grand Opening", "Exclusive merch, photocards & lightsticks", "Shop Now", 0xFFEC4899, 0xFFF472B6, 2),
)