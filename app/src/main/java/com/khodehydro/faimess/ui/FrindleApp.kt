package com.khodehydro.faimess.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.FeedUiState
import com.khodehydro.faimess.model.Person
import com.khodehydro.faimess.model.Story
import com.khodehydro.faimess.ui.components.AppHeader
import com.khodehydro.faimess.ui.components.FeedBottomSheet
import com.khodehydro.faimess.ui.components.FeedSheet
import com.khodehydro.faimess.ui.components.FeedTab
import com.khodehydro.faimess.ui.components.FloatingNavigation
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.components.StoryViewer
import com.khodehydro.faimess.ui.screens.CalendarScreen
import com.khodehydro.faimess.ui.screens.DiscoverScreen
import com.khodehydro.faimess.ui.screens.HomeScreen
import com.khodehydro.faimess.ui.screens.ProfileScreen
import com.khodehydro.faimess.ui.screens.SearchScreen
import com.khodehydro.faimess.ui.theme.FeedBackground
import com.khodehydro.faimess.ui.theme.FrindleTheme
import kotlinx.coroutines.launch

@Composable
fun FrindleApp(feedViewModel: FeedViewModel = viewModel()) {
    val state by feedViewModel.uiState.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(FeedTab.Home) }
    var sheet by rememberSaveable { mutableStateOf<FeedSheet?>(null) }
    var activePostId by rememberSaveable { mutableStateOf<String?>(null) }
    var storyPersonId by rememberSaveable { mutableStateOf<String?>(null) }
    val homeListState = rememberLazyListState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun notify(messageId: Int) { scope.launch { snackbar.showSnackbar(context.getString(messageId)) } }
    fun openPost(destination: FeedSheet, post: FeedPost) {
        activePostId = post.id
        sheet = destination
    }
    fun openPerson(person: Person) {
        sheet = null
        storyPersonId = person.id
    }
    fun share(post: FeedPost) {
        if (!sharePost(context, post)) notify(R.string.share_unavailable)
    }

    val interactions = PostInteractions(
        onLike = { feedViewModel.toggleLike(it.id) },
        onComments = { openPost(FeedSheet.Comments, it) },
        onShare = ::share,
        onOptions = { openPost(FeedSheet.PostMenu, it) },
        onMedia = { openPost(FeedSheet.Media, it) },
        onAuthor = ::openPerson,
    )

    BackHandler(enabled = tab != FeedTab.Home && sheet == null && storyPersonId == null) { tab = FeedTab.Home }

    FrindleScaffold(
        state = state,
        tab = tab,
        onTab = { tab = it },
        homeListState = homeListState,
        interactions = interactions,
        onCreate = { sheet = FeedSheet.CreatePost },
        onNotifications = { feedViewModel.readNotifications(); sheet = FeedSheet.Notifications },
        onStory = { storyPersonId = it.person.id },
        onAddStory = { sheet = FeedSheet.CreateStory },
        snackbar = snackbar,
    )

    val activePost = state.posts.find { it.id == activePostId }
    sheet?.let { destination ->
        FeedBottomSheet(
            destination = destination,
            post = activePost,
            onDismiss = { sheet = null },
            onPublish = { text, isStory ->
                val published = if (isStory) feedViewModel.publishStory(text) else feedViewModel.publishPost(text)
                if (published) {
                    sheet = null
                    tab = FeedTab.Home
                    if (isStory) storyPersonId = DemoContent.me.id
                    else scope.launch { homeListState.animateScrollToItem(0) }
                    notify(if (isStory) R.string.published_story else R.string.published_post)
                }
                published
            },
            onComment = { text -> activePost?.let { feedViewModel.addComment(it.id, text) } ?: false },
            onToggleSave = {
                activePost?.let {
                    feedViewModel.toggleSave(it.id)
                    notify(if (it.isSaved) R.string.unsaved_post else R.string.saved_post)
                }
                sheet = null
            },
            onShare = { sheet = null; activePost?.let(::share) },
            onPerson = ::openPerson,
        )
    }
    storyPersonId?.let { id ->
        StoryViewer(state.stories, id, onDismiss = { storyPersonId = null })
    }
}

@Composable
private fun FrindleScaffold(
    state: FeedUiState,
    tab: FeedTab,
    onTab: (FeedTab) -> Unit,
    homeListState: LazyListState,
    interactions: PostInteractions,
    onCreate: () -> Unit,
    onNotifications: () -> Unit,
    onStory: (Story) -> Unit,
    onAddStory: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val screenState = rememberSaveableStateHolder()
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .imePadding(),
    ) {
        Box(Modifier.align(Alignment.TopCenter).widthIn(max = 600.dp).fillMaxSize().background(FeedBackground)) {
            Column(Modifier.fillMaxSize()) {
                AppHeader(state.hasUnreadNotifications, onCreate, onNotifications)
                screenState.SaveableStateProvider(tab.name) {
                    when (tab) {
                        FeedTab.Home -> HomeScreen(state, homeListState, interactions, onStory, onAddStory, Modifier.fillMaxSize())
                        FeedTab.Discover -> DiscoverScreen(state.posts, interactions.onMedia, Modifier.fillMaxSize())
                        FeedTab.Calendar -> CalendarScreen({ onTab(FeedTab.Discover) }, Modifier.fillMaxSize())
                        FeedTab.Profile -> ProfileScreen(state.posts, interactions, Modifier.fillMaxSize())
                        FeedTab.Search -> SearchScreen(state.posts, interactions, Modifier.fillMaxSize())
                    }
                }
            }
            if (!keyboardVisible) {
                FloatingNavigation(
                    tab, onTab,
                    Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
            SnackbarHost(
                snackbar,
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = if (keyboardVisible) 8.dp else 86.dp),
            )
        }
    }
}

private fun sharePost(context: Context, post: FeedPost): Boolean = try {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text, post.author.name, post.caption))
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_chooser)))
    true
} catch (_: ActivityNotFoundException) {
    false
}

@Preview(name = "Reference · phone", widthDp = 393, heightDp = 852, showSystemUi = true)
@Preview(name = "Compact phone", widthDp = 320, heightDp = 700)
@Preview(name = "Large text", widthDp = 393, heightDp = 852, fontScale = 1.5f)
@Composable
private fun ReferenceFeedPreview() {
    FrindleTheme {
        FrindleScaffold(
            state = FeedUiState(DemoContent.posts, DemoContent.stories),
            tab = FeedTab.Home,
            onTab = {},
            homeListState = rememberLazyListState(),
            interactions = PostInteractions({}, {}, {}, {}, {}, {}),
            onCreate = {},
            onNotifications = {},
            onStory = {},
            onAddStory = {},
            snackbar = remember { SnackbarHostState() },
        )
    }
}
