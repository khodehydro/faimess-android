package com.khodehydro.faimess

import android.os.ParcelFileDescriptor
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.khodehydro.faimess.ui.components.PhotoReady
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class FrindleUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun referenceFeedAndFloatingNavigationAreVisible() {
        compose.onNodeWithText("Frindle").assertIsDisplayed()
        compose.onNodeWithTag("story_marcus").assertIsDisplayed()
        compose.onNodeWithTag("media_sienna-ridge").assertIsDisplayed()
        compose.onNodeWithTag("nav_home").assertIsSelected()
    }

    @Test fun likeTogglesAndSurvivesActivityRecreation() {
        compose.onNodeWithTag("like_sienna-ridge")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "26000 likes"))
            .performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("like_sienna-ridge")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
    }

    @Test fun rapidTapsDoNotDropLikeActions() {
        compose.onNodeWithTag("like_sienna-ridge").performTouchInput {
            repeat(7) {
                click()
                advanceEventTime(40)
            }
        }
        compose.onNodeWithTag("like_sienna-ridge")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "25999 likes"))
            .performClick()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "26000 likes"))
    }

    @Test fun navigatingAwayAfterALikeKeepsTheLatestPostState() {
        compose.onNodeWithTag("like_sienna-ridge").performClick()
        compose.onNodeWithTag("nav_discover").performClick()
        compose.onNodeWithTag("nav_home").performClick()
        compose.onNodeWithTag("like_sienna-ridge")
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
        compose.onNodeWithTag("profile_screen").performScrollToNode(hasTestTag("post_sienna-ridge"))
        compose.onNodeWithTag("post_sienna-ridge").assertIsDisplayed()
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
        compose.onNodeWithTag("post_sienna-ridge").assertIsDisplayed()
    }

    @Test fun videoPreviewDoesNotPretendToPlayVideo() {
        compose.onNodeWithTag("media_sienna-ridge").performClick()
        compose.onNodeWithText("Video preview").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.video_demo_note)).assertIsDisplayed()
    }

    @Test fun captureReferenceHomeScreenshot() {
        compose.onNodeWithTag("media_sienna-ridge").assertIsDisplayed()
        // Compose becoming idle does not imply that background bitmap decoding has finished.
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(SemanticsMatcher.expectValue(PhotoReady, false), useUnmergedTree = true)
                .fetchSemanticsNodes().isEmpty()
        }
        compose.waitForIdle()
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        // Test-only shell capture survives AGP uninstall/cleanup; the app itself needs no permission.
        val result = automation.executeShellCommand(
            "mkdir -p /sdcard/Download/frindle-screenshots && " +
                "screencap -p /sdcard/Download/frindle-screenshots/home.png && echo saved",
        )
        val output = ParcelFileDescriptor.AutoCloseInputStream(result).bufferedReader().use { it.readText() }
        assertEquals("saved", output.trim())
    }
}
