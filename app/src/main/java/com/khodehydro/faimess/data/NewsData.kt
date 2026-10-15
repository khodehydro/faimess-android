package com.khodehydro.faimess.data

import com.khodehydro.faimess.model.*

/** News items — matches src/data/feed.ts newsItems */
val ALL_NEWS = listOf(
    NewsItem(
        id = "nw1", title = "NOVAE announce first world tour \"Afterglow\"", tag = "Tour",
        source = "FAIMESS Desk", ago = "2 hrs ago", seed = 0,
        excerpt = "Twelve cities across Asia, Europe and North America, with the Seoul opener streaming live.",
        author = NewsAuthor("Mina Park", "Tour Reporter", 1),
        likes = 3420, views = 28400, commentsCount = 142,
        bodyKeys = listOf("news.nw1.p1", "news.nw1.p2", "news.nw1.p3"),
        comments = listOf(
            NewsComment("c1", "Minho", "@minho_k", 3, "1 hr ago", "Finally KSPO Dome! Getting tickets is going to be a warzone but I will be there.", 38,
                replies = listOf(
                    NewsComment("c1-r1", "Yuna", "@yuna_music", 5, "30 min ago", "Same here! Pre-sale starts 8 PM, set all your alarms!", 12,
                        replies = listOf(
                            NewsComment("c1-r1-1", "Minho", "@minho_k", 3, "15 min ago", "Got my 5G ready. Good luck to both of us!", 7)
                        ))
                )),
            NewsComment("c2", "Yuna", "@yuna_music", 5, "45 min ago", "Streaming the opener live in 4K on FAIMESS? Absolutely legendary.", 24),
            NewsComment("c3", "Seojin", "@seojin99", 4, "20 min ago", "Milan and Tokyo on the same run! This staging is going to be massive.", 15),
        ),
    ),
    NewsItem(
        id = "nw2", title = "AXION's \"Midnight Seoul\" tops the global chart", tag = "Charts",
        source = "Chart Watch", ago = "5 hrs ago", seed = 4,
        excerpt = "The lead single climbs to #1 in nine markets and breaks the group's first-week record.",
        author = NewsAuthor("Daniel Kim", "Charts Analyst", 4),
        likes = 4180, views = 34100, commentsCount = 189,
        bodyKeys = listOf("news.nw2.p1", "news.nw2.p2", "news.nw2.p3"),
        comments = listOf(
            NewsComment("c4", "Ari", "@ari_beats", 5, "3 hrs ago", "The production on this track is insane. Well deserved #1.", 52),
            NewsComment("c5", "Haru", "@haru_beats", 4, "2 hrs ago", "Nine markets! AXION world domination continues.", 31),
        ),
    ),
    NewsItem(
        id = "nw3", title = "PRISM9 \"Velvet Static\" album review: a bold new chapter", tag = "Editorial",
        source = "FAIMESS Desk", ago = "8 hrs ago", seed = 2,
        excerpt = "The girl group experiments with darker synths and sharper choreography in their third studio album.",
        author = NewsAuthor("Sori", "Music Editor", 0),
        likes = 2140, views = 19200, commentsCount = 96,
        bodyKeys = listOf("news.nw3.p1", "news.nw3.p2"),
        comments = listOf(
            NewsComment("c6", "Jennie", "@jxnnie_glow", 3, "6 hrs ago", "PRISM9 never disappoints. Velvet Static is album of the year.", 44),
        ),
    ),
    NewsItem(
        id = "nw4", title = "KAIROS wins \"Best New Artist\" at Seoul Music Awards", tag = "Awards",
        source = "Awards Wire", ago = "1 day ago", seed = 5,
        excerpt = "The rookie boy group takes home their first major award after just eight months since debut.",
        author = NewsAuthor("Jiwon Lee", "Awards Correspondent", 6),
        likes = 5870, views = 52300, commentsCount = 312,
        bodyKeys = listOf("news.nw4.p1", "news.nw4.p2", "news.nw4.p3"),
        comments = listOf(
            NewsComment("c7", "Sora", "@sora_sky", 6, "20 hrs ago", "KAIROS deserves every award. Their debut was incredible.", 88),
            NewsComment("c8", "Minho", "@minho_k", 7, "18 hrs ago", "Best New Artist AND Rookie of the Year. What a year for KAIROS.", 65),
        ),
    ),
)