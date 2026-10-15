package com.khodehydro.faimess.data

import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.Person
import com.khodehydro.faimess.model.Playlist
import com.khodehydro.faimess.model.Story
import com.khodehydro.faimess.model.Track

/**
 * Offline fixture data, intentionally isolated from composables for a future repository/API.
 * Tracks, artists, albums, durations and artwork are the FAIMESS web app's own demo catalogue
 * (`src/data/feed.ts`, `src/data/player.ts`, `src/assets/photos`), so the Android feed shows the
 * same songs the site plays and the shapes match what `/api/tracks` and `/api/social/playlists`
 * return.
 */
object DemoContent {
    val me = Person("me", "Esther Howard", R.drawable.avatar_me)
    val marcus = Person("marcus", "Marcus", R.drawable.avatar_marcus)
    val abdullah = Person("abdullah", "Abdullah", R.drawable.avatar_abdullah)
    val sienna = Person("sienna", "Sienna", R.drawable.avatar_sienna)
    val alex = Person("alex", "Alex", R.drawable.avatar_alex)
    val jo = Person("jo", "Joanna", R.drawable.avatar_jo)

    val stories = listOf(
        Story(me, R.drawable.forest_lake, "A little escape, a lot of green.", isSelf = true),
        Story(marcus, R.drawable.mountain_ridge, "Taking the scenic route.", isLive = true),
        Story(abdullah, R.drawable.forest_lake, "Slow mornings by the water."),
        Story(sienna, R.drawable.mountain_ridge, "Some views are worth the climb."),
        Story(alex, R.drawable.forest_lake, "Meet me where the trees are."),
        Story(jo, R.drawable.mountain_ridge, "Another day, another little adventure."),
    )

    // The site's demo catalogue: ids are the shelf ids the web player resolves (nt* newest, tr* trending).
    val afterimage = Track("nt7", "Afterimage", "NOVAE", "Afterimage · single", 204, R.drawable.album_afterimage)
    val afterglow = Track("nt1", "Afterglow", "NOVAE", "Afterglow", 192, R.drawable.album_afterglow)
    val midnightSeoul = Track("nt2", "Midnight Seoul", "AXION", "Midnight Seoul · single", 208, R.drawable.album_midnight_seoul)
    val paperHeart = Track("nt3", "Paper Heart", "SEORA", "Paper Heart · single", 178, R.drawable.album_paper_heart)
    val neonBloom = Track("nt4", "Neon Bloom", "LUNEX", "Neon Bloom · single", 185, R.drawable.album_neon_bloom)
    val haloDrive = Track("nt5", "Halo Drive", "PRISM9", "Velvet Static", 221, R.drawable.album_velvet_static)
    val silverHour = Track("nt6", "Silver Hour", "HANEUL", "Tokyo Window", 242, R.drawable.album_tokyo_window)
    val cherryStatic = Track("tr3", "Cherry Static", "PRISM9", "Cherry Static · single", 199, R.drawable.album_cherry_static)
    val gravity = Track("tr5", "Gravity", "VELVET MOON", "Nightbloom", 216, R.drawable.album_nightbloom)
    val blueSignal = Track("tr6", "Blue Signal", "KAIROS", "Blue Hour", 182, R.drawable.album_blue_hour)

    val tracks = listOf(
        afterimage, afterglow, midnightSeoul, paperHeart, neonBloom, haloDrive, silverHour, cherryStatic, gravity, blueSignal,
    )

    // Names, moods and covers follow the site's playlist roster and its six bundled cover crops.
    val midnightDrive = Playlist(
        id = "pl-sienna-midnight-drive",
        name = "Midnight Seoul Drive",
        mood = "Mellow",
        cover = R.drawable.playlist_midnight_drive,
        tracks = listOf(midnightSeoul, afterglow, silverHour, gravity, neonBloom, blueSignal),
    )
    val rainyWindow = Playlist(
        id = "pl-esther-rainy-window",
        name = "Rainy Window",
        mood = "Soft",
        cover = R.drawable.playlist_rainy_window,
        tracks = listOf(paperHeart, silverHour, afterimage, blueSignal),
    )
    val comebackCountdown = Playlist(
        id = "pl-alex-comeback",
        name = "Comeback Countdown",
        mood = "Hype",
        cover = R.drawable.playlist_comeback,
        tracks = listOf(cherryStatic, haloDrive, afterimage, midnightSeoul, neonBloom),
    )
    val weekendReset = Playlist(
        id = "pl-esther-weekend-reset",
        name = "Weekend Reset",
        mood = "Warm",
        cover = R.drawable.playlist_weekend_reset,
        tracks = listOf(afterglow, neonBloom, paperHeart),
    )

    val posts = listOf(
        FeedPost(
            id = "sienna-drive",
            author = sienna,
            time = "1H ago",
            caption = "Late drive through the city with the windows down. AXION opens it, HANEUL closes it, " +
                "and Gravity is the song you save for the last bridge before home. " +
                "Drop your own night songs in the comments and I will add the best ones.",
            playlist = midnightDrive,
            likes = 26_000,
            commentCount = 1_000,
            shares = 381,
            isLiked = true,
        ),
        FeedPost(
            id = "esther-rain",
            author = me,
            time = "4H ago",
            caption = "Soft songs for a grey afternoon: SEORA, HANEUL and a little NOVAE at the end. " +
                "Save this one for the next rainy day and tell me what you would add.",
            playlist = rainyWindow,
            likes = 4_820,
            commentCount = 128,
            shares = 64,
        ),
        FeedPost(
            id = "alex-comeback",
            author = alex,
            time = "6H ago",
            caption = "Counting down to comeback week with PRISM9 on repeat. " +
                "Who is on your hype list this month?",
            playlist = comebackCountdown,
            likes = 2_416,
            commentCount = 86,
            shares = 29,
        ),
    )

    fun localPost(id: String, caption: String) = FeedPost(
        id = id,
        author = me,
        time = "Just now",
        caption = caption,
        playlist = weekendReset,
        likes = 0,
        commentCount = 0,
        shares = 0,
    )
}
