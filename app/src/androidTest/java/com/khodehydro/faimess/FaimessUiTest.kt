package com.khodehydro.faimess

import android.os.ParcelFileDescriptor
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.khodehydro.faimess.ui.components.PhotoReady
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class FaimessUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun referenceFeedAndFloatingNavigationAreVisible() {
        compose.onNodeWithText("FAIMESS").assertIsDisplayed()
        compose.onNodeWithTag("story_marcus").assertIsDisplayed()
        compose.onNodeWithTag("playlist_sienna-drive").assertIsDisplayed()
        compose.onNodeWithTag("nav_home").assertIsSelected()
    }

    @Test fun likeTogglesAndSurvivesActivityRecreation() {
        compose.onNodeWithTag("like_sienna-drive")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "26000 likes"))
            .performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("like_sienna-drive")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
    }

    @Test fun rapidTapsDoNotDropLikeActions() {
        compose.onNodeWithTag("like_sienna-drive").performTouchInput {
            repeat(7) {
                click()
                advanceEventTime(40)
            }
        }
        compose.onNodeWithTag("like_sienna-drive")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
            .performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "26000 likes"))
    }

    @Test fun navigatingAwayAfterALikeKeepsTheLatestPostState() {
        compose.onNodeWithTag("like_sienna-drive").performClick()
        compose.onNodeWithTag("nav_discover").performClick()
        compose.onNodeWithTag("nav_home").performClick()
        compose.onNodeWithTag("like_sienna-drive")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
        compose.onNodeWithContentDescription("Options for Sienna’s post").performClick()
        compose.onNodeWithTag("save_post").performClick()
        compose.onNodeWithContentDescription("Options for Sienna’s post").performClick()
        compose.onNodeWithText("Remove from saved").assertIsDisplayed()
    }

    @Test fun storyOpensAndCanBeDismissed() {
        compose.onNodeWithTag("story_sienna").performClick()
        compose.onNodeWithTag("story_viewer").assertIsDisplayed()
        compose.onNodeWithContentDescription("Next story").performClick()
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithTag("story_viewer").assertDoesNotExist()
    }

    @Test fun commentCanBeAddedLocally() {
        compose.onNodeWithContentDescription("Comments on Sienna’s post").performClick()
        compose.onNodeWithTag("send_comment").assertIsNotEnabled()
        compose.onNodeWithTag("comment_input").performTextInput("What a beautiful view!")
        closeSoftKeyboard()
        compose.onNodeWithTag("send_comment").performClick()
        compose.onNodeWithText("What a beautiful view!").assertIsDisplayed()
    }

    @Test fun composerRejectsBlankAndPublishesAtTopOfFeed() {
        compose.onNodeWithTag("create_post").performClick()
        compose.onNodeWithTag("publish_button").assertIsNotEnabled()
        compose.onNodeWithTag("composer_input").performTextInput("Hello from my next adventure")
        closeSoftKeyboard()
        compose.onNodeWithTag("publish_button").performScrollTo().performClick()
        compose.onNodeWithTag("post_local-0").assertIsDisplayed()
        compose.onNodeWithText("Hello from my next adventure").assertIsDisplayed()
    }

    @Test fun savedPostAppearsInProfile() {
        compose.onNodeWithContentDescription("Options for Sienna’s post").performClick()
        compose.onNodeWithTag("save_post").performClick()
        compose.onNodeWithTag("nav_profile").performClick()
        compose.onNodeWithText("Saved").performClick()
        compose.onNodeWithTag("profile_screen").performScrollToNode(hasTestTag("post_sienna-drive"))
        compose.onNodeWithTag("post_sienna-drive").assertIsDisplayed()
    }

    @Test fun allNavigationDestinationsAreConnected() {
        listOf("discover", "calendar", "profile", "search").forEach { name ->
            compose.onNodeWithTag("nav_$name").performClick()
            compose.onNodeWithTag("${name}_screen").assertIsDisplayed()
            compose.onNodeWithTag("nav_$name").assertIsSelected()
        }
        compose.onNodeWithTag("nav_home").performClick()
        compose.onNodeWithTag("home_feed").assertIsDisplayed()
    }

    @Test fun searchHasAnHonestEmptyState() {
        compose.onNodeWithTag("nav_search").performClick()
        compose.onNodeWithTag("search_input").performTextInput("no-such-place")
        closeSoftKeyboard()
        compose.onNodeWithText("Nothing here just yet").assertIsDisplayed()
        compose.onNodeWithContentDescription("Clear search").performClick()
        compose.onNodeWithTag("post_sienna-drive").assertIsDisplayed()
    }

    @Test fun tappingAPlaylistPostOpensItsPageAndBackReturnsToTheFeed() {
        compose.onNodeWithTag("playlist_sienna-drive").performClick()
        compose.onNodeWithTag("playlist_screen").assertIsDisplayed()
        // Neon Bloom is the fifth song: hidden behind "+2 more tracks" in the card, listed on the page.
        compose.onNodeWithTag("playlist_screen").performScrollToNode(hasTestTag("track_nt4"))
        compose.onNodeWithTag("track_nt4").assertIsDisplayed()
        compose.onNodeWithTag("playlist_play").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.playback_not_connected)).assertIsDisplayed()
        compose.onNodeWithTag("playlist_back").performClick()
        compose.onNodeWithTag("playlist_screen").assertDoesNotExist()
        compose.onNodeWithTag("home_feed").assertIsDisplayed()
    }

    @Test fun likingOnThePlaylistPageIsTheSameLikeAsInTheFeed() {
        compose.onNodeWithTag("playlist_sienna-drive").performClick()
        compose.onNodeWithTag("playlist_like")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "26000 likes"))
            .performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
        Espresso.pressBack()
        compose.onNodeWithTag("like_sienna-drive")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
    }

    @Test fun discoverTilesOpenTheSamePlaylistPage() {
        compose.onNodeWithTag("nav_discover").performClick()
        compose.onNodeWithTag("discover_screen").performScrollToNode(hasTestTag("tile_alex-comeback"))
        compose.onNodeWithTag("tile_alex-comeback").performClick()
        compose.onNodeWithTag("playlist_screen").assertIsDisplayed()
        // The tile underneath still carries the same name; look only inside the page.
        compose.onNode(hasText("Comeback Countdown") and hasAnyAncestor(hasTestTag("playlist_screen"))).assertIsDisplayed()
    }

    @Test fun selectedTabUsesTheFilledIconAndSearchLivesOutsideTheMenu() {
        compose.onNodeWithTag("nav_home").assertIsSelected()
        compose.onNodeWithTag("nav_search").performClick()
        compose.onNodeWithTag("nav_search").assertIsSelected()
        compose.onNodeWithTag("nav_home").assertIsNotSelected()
        compose.onNodeWithTag("search_screen").assertIsDisplayed()
    }

    @Test fun captureReferenceHomeScreenshot() {
        compose.onNodeWithTag("playlist_sienna-drive").assertIsDisplayed()
        // Compose becoming idle does not imply that background bitmap decoding has finished.
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(SemanticsMatcher.expectValue(PhotoReady, false), useUnmergedTree = true)
                .fetchSemanticsNodes().isEmpty()
        }
        compose.waitForIdle()
        // Test-only shell capture survives AGP uninstall/cleanup; the app itself needs no permission.
        // UiAutomation starts each command directly, without a shell, so operators such as `&&`
        // and `echo` are passed to the first program as plain arguments: run one command per step.
        shell("mkdir -p $SCREENSHOT_DIR")
        shell("screencap -p $SCREENSHOT_FILE")
        val size = shell("stat -c %s $SCREENSHOT_FILE").trim()
        assertTrue("screencap did not write $SCREENSHOT_FILE (stat: '$size')", (size.toLongOrNull() ?: 0L) > 0L)
    }

    /** Runs one plain command as the shell user and returns its standard output once it exits. */
    private fun shell(command: String): String {
        val output = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(output).bufferedReader().use { it.readText() }
    }

    private companion object {
        const val SCREENSHOT_DIR = "/sdcard/Download/faimess-screenshots"
        const val SCREENSHOT_FILE = "$SCREENSHOT_DIR/home.png"
    }
}
