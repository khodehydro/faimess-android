package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedActions
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.LocalBottomChromeHeight
import com.khodehydro.faimess.ui.components.PostCard
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.theme.InkMuted

@Composable
fun SearchScreen(posts: List<FeedPost>, interactions: PostInteractions, modifier: Modifier = Modifier) {
    var query by rememberSaveable { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val results = remember(posts, query) { FeedActions.search(posts, query) }
    Column(modifier = modifier.testTag("search_screen")) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.search_title), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = query,
                onValueChange = { query = it.take(100) },
                modifier = Modifier.fillMaxWidth().testTag("search_input"),
                placeholder = { Text(stringResource(R.string.search_hint), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { AppIcon(R.drawable.ic_search) },
                trailingIcon = if (query.isNotEmpty()) {
                    { IconButton(onClick = { query = "" }) { AppIcon(R.drawable.ic_close, stringResource(R.string.clear_search)) } }
                } else null,
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            )
            Text(
                if (query.isBlank()) stringResource(R.string.search_suggestions) else stringResource(R.string.search_results, results.size),
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted,
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = LocalBottomChromeHeight.current),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (results.isEmpty()) {
                item { EmptyState(R.drawable.ic_search, stringResource(R.string.no_results), stringResource(R.string.no_results_description)) }
            }
            items(results, key = { it.id }, contentType = { "post" }) { post -> PostCard(post, interactions) }
        }
    }
}
