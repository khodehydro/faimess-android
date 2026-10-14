package com.khodehydro.faimess.data

import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.Person
import com.khodehydro.faimess.model.Story

/** Offline fixture data, intentionally isolated from composables for a future repository/API. */
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

    val posts = listOf(
        FeedPost(
            id = "sienna-ridge",
            author = sienna,
            time = "1H ago",
            caption = "More fresh new travel content inspo for all you travel bloggers or creators " +
                "working but as always, the best moments happen off the beaten path. " +
                "A little green, a little quiet, and a view worth every step.",
            image = R.drawable.mountain_ridge,
            imageDescription = "A stone trail along a vivid green mountain ridge, disappearing into clouds",
            likes = 26_000,
            commentCount = 1_000,
            shares = 381,
            isVideoPreview = true,
            isLiked = true,
        ),
        FeedPost(
            id = "esther-lake",
            author = me,
            time = "4H ago",
            caption = "More fresh new travel content inspo for all you travel bloggers or creators " +
                "working but as for me, a quiet morning by the lake is all the inspiration I need. " +
                "Save this little escape for your next adventure.",
            image = R.drawable.forest_lake,
            imageDescription = "An emerald lake, a small wooden boat, and misty forest-covered mountains",
            likes = 4_820,
            commentCount = 128,
            shares = 64,
        ),
        FeedPost(
            id = "alex-weekend",
            author = alex,
            time = "6H ago",
            caption = "Out of office, into the green. A reminder to take the long way home this weekend. " +
                "Who would you bring on this trail?",
            image = R.drawable.mountain_ridge,
            imageDescription = "Lush green hills and a narrow hiking trail beneath the mist",
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
        image = R.drawable.forest_lake,
        imageDescription = "The bundled sample photo of a peaceful green mountain lake",
        likes = 0,
        commentCount = 0,
        shares = 0,
    )
}
