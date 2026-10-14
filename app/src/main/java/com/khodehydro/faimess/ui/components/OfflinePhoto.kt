package com.khodehydro.faimess.ui.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.theme.SoftSurface
import kotlinx.coroutines.awaitCancellation

// Test-only metadata, not a spoken accessibility label. Screenshots wait for real decoded images.
val PhotoReady = SemanticsPropertyKey<Boolean>("PhotoReady")
var SemanticsPropertyReceiver.photoReady by PhotoReady

/**
 * The bundled photos are bounded (192px portraits, <=1200px scenes). Requesting their original
 * size lets the same decoded bitmap serve the feed, grid, story and sheet, rather than decoding
 * a new size at each tap. Coil's shared, bounded memory cache owns the bitmaps; decoding is async.
 * Only local resource IDs are accepted: this component never needs a network connection.
 */
fun offlinePhotoRequest(context: Context, @DrawableRes resource: Int): ImageRequest =
    ImageRequest.Builder(context.applicationContext)
        .data(resource)
        .size(Size.ORIGINAL)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(false)
        .build()

@Composable
fun OfflinePhoto(
    @DrawableRes resource: Int,
    description: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    // Layoutlib previews are not an app lifecycle. Keep them deterministic without async work.
    if (LocalInspectionMode.current) {
        Image(painterResource(resource), description, modifier, contentScale = contentScale)
        return
    }
    val context = LocalContext.current.applicationContext
    val request = remember(context, resource) { offlinePhotoRequest(context, resource) }
    val placeholder = remember { ColorPainter(SoftSurface) }
    var ready by remember(request) { mutableStateOf(false) }
    AsyncImage(
        model = request,
        contentDescription = description,
        imageLoader = context.imageLoader,
        placeholder = placeholder,
        contentScale = contentScale,
        onSuccess = { ready = true },
        modifier = modifier.semantics { photoReady = ready },
    )
}

/** Warm the two shared scenes after the first frame, without delaying a tap or blocking the UI. */
@Composable
fun PreloadFeedPhotos() {
    if (LocalInspectionMode.current) return
    val context = LocalContext.current.applicationContext
    val imageLoader = context.imageLoader
    LaunchedEffect(context, imageLoader) {
        withFrameNanos { }
        val requests = listOf(R.drawable.mountain_ridge, R.drawable.forest_lake).map { resource ->
            imageLoader.enqueue(offlinePhotoRequest(context, resource))
        }
        try {
            awaitCancellation()
        } finally {
            requests.forEach { it.dispose() }
        }
    }
}
