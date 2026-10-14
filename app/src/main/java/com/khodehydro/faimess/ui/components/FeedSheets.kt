package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedActions
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.Person
import com.khodehydro.faimess.ui.theme.HeartRed
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.MutedInk

/** Secondary UI lives in dismissible native sheets; the launch surface remains the reference feed. */
enum class FeedSheet { CreatePost, CreateStory, Comments, PostMenu, Notifications, Media }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedBottomSheet(
    destination: FeedSheet,
    post: FeedPost?,
    onDismiss: () -> Unit,
    onPublish: (String, Boolean) -> Boolean,
    onComment: (String) -> Boolean,
    onToggleSave: () -> Unit,
    onShare: () -> Unit,
    onPerson: (Person) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        contentColor = Ink,
        scrimColor = Color.Black.copy(alpha = 0.3f),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        modifier = Modifier.imePadding(),
    ) {
        val title = when (destination) {
            FeedSheet.CreatePost -> R.string.create_post
            FeedSheet.CreateStory -> R.string.your_story
            FeedSheet.Comments -> R.string.comments
            FeedSheet.PostMenu -> R.string.post_menu
            FeedSheet.Notifications -> R.string.notifications
            FeedSheet.Media -> if (post?.isVideoPreview == true) R.string.video_preview else R.string.photo_preview
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    AppIcon(R.drawable.ic_close, stringResource(R.string.close))
                }
            }
            when (destination) {
                FeedSheet.CreatePost, FeedSheet.CreateStory -> Composer(
                    isStory = destination == FeedSheet.CreateStory,
                    onPublish = { onPublish(it, destination == FeedSheet.CreateStory) },
                )
                FeedSheet.Comments -> post?.let { Comments(it, onComment) }
                FeedSheet.PostMenu -> post?.let {
                    ListItem(
                        headlineContent = { Text(stringResource(if (it.isSaved) R.string.unsave_post else R.string.save_post)) },
                        leadingContent = { AppIcon(if (it.isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark) },
                        modifier = Modifier.clickable(onClick = onToggleSave).testTag("save_post"),
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.share)) },
                        leadingContent = { AppIcon(R.drawable.ic_send) },
                        modifier = Modifier.clickable(onClick = onShare),
                    )
                }
                FeedSheet.Notifications -> Notifications(onPerson)
                FeedSheet.Media -> post?.let { MediaPreview(it) }
            }
        }
    }
}

@Composable
private fun Composer(isStory: Boolean, onPublish: (String) -> Boolean) {
    var draft by rememberSaveable(isStory) { mutableStateOf("") }
    var limitReached by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.composer_note), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
        OutlinedTextField(
            value = draft,
            onValueChange = { if (it.length <= FeedActions.MAX_CAPTION_LENGTH) draft = it },
            modifier = Modifier.fillMaxWidth().testTag("composer_input"),
            placeholder = { Text(stringResource(if (isStory) R.string.story_composer_hint else R.string.post_composer_hint)) },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(18.dp),
            supportingText = { Text(stringResource(R.string.character_count, draft.length, FeedActions.MAX_CAPTION_LENGTH)) },
        )
        OfflinePhoto(
            R.drawable.forest_lake,
            stringResource(R.string.photo_demo_note),
            Modifier.fillMaxWidth().aspectRatio(2.4f).clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop,
        )
        if (limitReached) Text(stringResource(R.string.demo_limit), color = HeartRed, style = MaterialTheme.typography.bodySmall)
        Button(
            onClick = { limitReached = !onPublish(draft) },
            enabled = draft.isNotBlank(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("publish_button"),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(stringResource(if (isStory) R.string.publish_story else R.string.publish_post))
        }
    }
}

@Composable
private fun Comments(post: FeedPost, onComment: (String) -> Boolean) {
    var draft by rememberSaveable(post.id) { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
        Text(stringResource(R.string.comments_note), style = MaterialTheme.typography.bodySmall, color = MutedInk)
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (post.localComments.isEmpty()) {
                item {
                    EmptyState(R.drawable.ic_comment, stringResource(R.string.no_comments), stringResource(R.string.no_comments_description))
                }
            }
            items(post.localComments) { comment ->
                Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Avatar(DemoContent.me, 32.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(comment.author, style = MaterialTheme.typography.titleSmall)
                        Text(comment.body, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (error) Text(stringResource(R.string.comment_error), color = HeartRed, style = MaterialTheme.typography.bodySmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = draft,
                onValueChange = { if (it.length <= FeedActions.MAX_COMMENT_LENGTH) draft = it },
                modifier = Modifier.weight(1f).testTag("comment_input"),
                placeholder = { Text(stringResource(R.string.comment_hint), style = MaterialTheme.typography.bodyMedium) },
                shape = RoundedCornerShape(18.dp),
                maxLines = 3,
                supportingText = { Text(stringResource(R.string.character_count, draft.length, FeedActions.MAX_COMMENT_LENGTH)) },
            )
            IconButton(
                enabled = draft.isNotBlank(),
                onClick = {
                    error = !onComment(draft)
                    if (!error) draft = ""
                },
                modifier = Modifier.size(48.dp).testTag("send_comment"),
            ) {
                AppIcon(R.drawable.ic_send, stringResource(R.string.send_comment))
            }
        }
    }
}

@Composable
private fun Notifications(onPerson: (Person) -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(stringResource(R.string.notifications_note), style = MaterialTheme.typography.bodySmall, color = MutedInk)
        listOf(
            DemoContent.sienna to R.string.notification_sienna,
            DemoContent.marcus to R.string.notification_marcus,
            DemoContent.alex to R.string.notification_alex,
        ).forEach { (person, text) ->
            ListItem(
                headlineContent = { Text(stringResource(text), style = MaterialTheme.typography.bodyMedium) },
                supportingContent = { Text(stringResource(R.string.notification_recent), style = MaterialTheme.typography.bodySmall) },
                leadingContent = { Avatar(person, 38.dp) },
                modifier = Modifier.clickable { onPerson(person) },
            )
        }
    }
}

@Composable
private fun MediaPreview(post: FeedPost) {
    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OfflinePhoto(
            post.image, post.imageDescription,
            Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(post.author.name, style = MaterialTheme.typography.titleMedium)
        Text(
            stringResource(if (post.isVideoPreview) R.string.video_demo_note else R.string.photo_demo_note),
            style = MaterialTheme.typography.bodyMedium,
            color = MutedInk,
        )
    }
}
