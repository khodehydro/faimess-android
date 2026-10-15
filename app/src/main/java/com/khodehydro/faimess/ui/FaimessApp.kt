package com.khodehydro.faimess.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.derivedStateOf
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
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.ui.components.AppHeader
import com.khodehydro.faimess.ui.components.FeedBottomSheet
import com.khodehydro.faimess.ui.components.FeedSheet
import com.khodehydro.faimess.ui.components.FeedTab
import com.khodehydro.faimess.ui.components.FloatingNavigation
import com.khodehydro.faimess.ui.components.MiniPlayer
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.components.PreloadFeedPhotos
import com.khodehydro.faimess.ui.components.StoryViewer
import com.khodehydro.faimess.ui.screens.CalendarScreen
import com.khodehydro.faimess.ui.screens.DiscoverScreen
import com.khodehydro.faimess.ui.screens.HomeScreen
import com.khodehydro.faimess.ui.screens.PlayerScreen
import com.khodehydro.faimess.ui.screens.PlaylistScreen
import com.khodehydro.faimess.ui.screens.ProfileScreen
import com.khodehydro.faimess.ui.screens.SearchScreen
import com.khodehydro.faimess.ui.theme.CanvasBackground
import com.khodehydro.faimess.ui.theme.FaimessTheme
import kotlinx.coroutines.launch

@Composable
fun FaimessApp(
    feedViewModel: FeedViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    PreloadFeedPhotos()
    val state by feedViewModel.uiState.collectAsStateWithLifecycle()
    val playerState by playbackViewModel.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(FeedTab.Home) }
    var sheet by rememberSaveable { mutableStateOf<FeedSheet?>(null) }
    var activePostId by rememberSaveable { mutableStateOf<String?>(null) }
    var storyPersonId by rememberSaveable { mutableStateOf<String?>(null) }
    var openPlaylistPostId by rememberSaveable { mutableStateOf<String?>(null) }
    var openPlayer by rememberSaveable { mutableStateOf(false) }
    val homeListState = rememberLazyListState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val feedback = remember(snackbar, scope) { LatestSnackbar(snackbar, scope) }

    fun notify(messageId: Int) { feedback.show(context.getString(messageId)) }
    fun openPost(destination: FeedSheet, postId: String) {
        activePostId = postId
        sheet = destination
    }
    fun openPerson(person: Person) {
        sheet = null
        storyPersonId = person.id
    }
    fun share(post: FeedPost) {
        if (!sharePost(context, post)) notify(R.string.share_unavailable)
    }
    fun playTrack(track: Track, playlist: List<Track>) {
        playbackViewModel.play(track, playlist)
    }
    fun playPlaylist(playlist: List<Track>) {
        if (playlist.isNotEmpty()) playbackViewModel.play(playlist.first(), playlist)
    }

    // These handlers only capture the ViewModel, context and stable MutableState delegates.
    // Reuse them across likes, comments, overlays and tab changes so unchanged cards can skip work.
    val interactions = remember(feedViewModel, context, feedback) {
        PostInteractions(
            onLike = feedViewModel::toggleLike,
            onComments = { openPost(FeedSheet.Comments, it) },
            onShare = { postId -> feedViewModel.uiState.value.posts.find { it.id == postId }?.let(::share) },
            onOptions = { openPost(FeedSheet.PostMenu, it) },
            onOpen = { openPlaylistPostId = it },
            onAuthor = ::openPerson,
            onPlayTrack = { track, playlist -> playTrack(track, playlist) },
        )
    }

    BackHandler(enabled = tab != FeedTab.Home && sheet == null && storyPersonId == null && openPlaylistPostId == null && !openPlayer) {
        tab = FeedTab.Home
    }

    FaimessScaffold(
        state = state,
        playerState = playerState,
        tab = tab,
        // The menu stays visible on the playlist page; choosing any tab leaves the page.
        onTab = { tab = it; openPlaylistPostId = null },
        homeListState = homeListState,
        interactions = interactions,
        onCreate = { sheet = FeedSheet.CreatePost },
        onNotifications = { feedViewModel.readNotifications(); sheet = FeedSheet.Notifications },
        onStory = { storyPersonId = it.person.id },
        onAddStory = { sheet = FeedSheet.CreateStory },
        snackbar = snackbar,
        openPlaylistPost = openPlaylistPostId?.let { id -> state.posts.find { it.id == id } },
        onClosePlaylist = { openPlaylistPostId = null },
        onPlay = { playlist -> playPlaylist(playlist) },
        onPlayTrack = { track, playlist -> playTrack(track, playlist) },
        onMiniPlayerToggle = { playbackViewModel.togglePlayPause() },
        onMiniPlayerNext = { playbackViewModel.next() },
        onOpenPlayer = { openPlayer = true },
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
                    else scope.launch { homeListState.scrollToItem(0) }
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
            onPerson = interactions.onAuthor,
        )
    }
    storyPersonId?.let { id ->
        StoryViewer(state.stories, id, onDismiss = { storyPersonId = null })
    }

    // Full-screen player overlay
    AnimatedVisibility(
        visible = openPlayer && playerState.hasTrack,
        enter = slideInVertically(tween(350)) { -it / 3 } + fadeIn(tween(350)),
        exit = slideOutVertically(tween(300)) { -it / 3 } + fadeOut(tween(300)),
    ) {
        PlayerScreen(
            state = playerState,
            onBack = { openPlayer = false },
            onTogglePlayPause = { playbackViewModel.togglePlayPause() },
            onNext = { playbackViewModel.next() },
            onPrevious = { playbackViewModel.previous() },
            onSeek = { playbackViewModel.seekTo(it) },
            onSeekSeconds = { playbackViewModel.seekToSeconds(it) },
            onShuffle = { playbackViewModel.toggleShuffle() },
            onRepeat = { playbackViewModel.toggleRepeat() },
            onFavourite = { playbackViewModel.toggleFavourite() },
            onPlayTrack = { track -> playbackViewModel.playTrackInQueue(track) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun FaimessScaffold(
    state: FeedUiState,
    playerState: PlayerUiState,
    tab: FeedTab,
    onTab: (FeedTab) -> Unit,
    homeListState: LazyListState,
    interactions: PostInteractions,
    onCreate: () -> Unit,
    onNotifications: () -> Unit,
    onStory: (Story) -> Unit,
    onAddStory: () -> Unit,
    snackbar: SnackbarHostState,
    openPlaylistPost: FeedPost? = null,
    onClosePlaylist: () -> Unit = {},
    onPlay: (List<Track>) -> Unit = {},
    onPlayTrack: (Track, List<Track>) -> Unit = { _, _ -> },
    onMiniPlayerToggle: () -> Unit = {},
    onMiniPlayerNext: () -> Unit = {},
    onOpenPlayer: () -> Unit = {},
) {
    val screenState = rememberSaveableStateHolder()
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    // Insets change on every keyboard-animation frame; only visibility affects the chrome.
    val keyboardVisible by remember(ime, density) {
        derivedStateOf { ime.getBottom(density) > 0 }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .imePadding(),
    ) {
        Box(Modifier.align(Alignment.TopCenter).widthIn(max = 600.dp).fillMaxSize().background(CanvasBackground)) {
            Column(Modifier.fillMaxSize()) {
                AppHeader(state.hasUnreadNotifications, onCreate, onNotifications)
                screenState.SaveableStateProvider(tab.name) {
                    when (tab) {
                        FeedTab.Home -> HomeScreen(state, homeListState, interactions, onStory, onAddStory, Modifier.fillMaxSize())
                        FeedTab.Discover -> DiscoverScreen(state.posts, interactions.onOpen, Modifier.fillMaxSize())
                        FeedTab.Calendar -> CalendarScreen({ onTab(FeedTab.Discover) }, Modifier.fillMaxSize())
                        FeedTab.Profile -> ProfileScreen(state.posts, interactions, Modifier.fillMaxSize())
                        FeedTab.Search -> SearchScreen(state.posts, interactions, Modifier.fillMaxSize())
                    }
                }
            }
            // A playlist opens as a page over the current tab, under the floating menu; Back returns
            // to where the user was. Other posts' playlists are offered further down the page.
            openPlaylistPost?.let { post ->
                val suggestions = remember(state.posts, post.id) { state.posts.filter { it.id != post.id }.take(6) }
                PlaylistScreen(
                    post, suggestions, interactions, onClosePlaylist,
                    onPlay = { onPlay(post.playlist.tracks) },
                    onPlayTrack = { track -> onPlayTrack(track, post.playlist.tracks) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (!keyboardVisible) {
                // Mini player + floating nav in a Column so they share the same width
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MiniPlayer(
                        state = playerState,
                        onTogglePlayPause = onMiniPlayerToggle,
                        onNext = onMiniPlayerNext,
                        onOpen = onOpenPlayer,
                    )
                    FloatingNavigation(tab, onTab)
                }
            }
            SnackbarHost(
                snackbar,
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = if (keyboardVisible) 8.dp else 100.dp),
            )
        }
    }
}

private fun sharePost(context: Context, post: FeedPost): Boolean = try {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text, post.author.name, post.playlist.name, post.caption))
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
    FaimessTheme {
        FaimessScaffold(
            state = FeedUiState(DemoContent.posts, DemoContent.stories),
            playerState = PlayerUiState(),
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