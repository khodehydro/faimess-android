package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

/** The site clips the square brand artwork to this corner everywhere it shows the logo. */
private val BrandMarkShape = RoundedCornerShape(CornerSize(32.5f))

/**
 * The FAIMESS mark exactly as the website draws it: the brand artwork (white cat on the brand
 * purple) rendered from the same PNG, with rounded corners instead of a redrawn vector.
 */
@Composable
fun BrandMark(size: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.brand_logo),
        contentDescription = null,
        modifier = modifier.size(size).clip(BrandMarkShape).background(Primary),
    )
}

@Composable
fun AppHeader(hasUnreadNotifications: Boolean, onCreate: () -> Unit, onNotifications: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).height(70.dp)
            .padding(start = 18.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandMark(31.dp)
        Spacer(Modifier.width(8.dp))
        // The wordmark matches the site: extra-bold, uppercase name, -0.02em tracking.
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.02).em,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(onClick = onCreate, modifier = Modifier.size(48.dp).testTag("create_post")) {
                Box(Modifier.size(40.dp).background(Subtle, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(R.drawable.ic_add_circle, stringResource(R.string.create_post), Modifier.size(19.dp), Ink)
                }
            }
            IconButton(onClick = onNotifications, modifier = Modifier.size(48.dp)) {
                Box(Modifier.size(40.dp).background(Subtle, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(
                        R.drawable.ic_bell,
                        stringResource(if (hasUnreadNotifications) R.string.unread_notifications else R.string.notifications),
                        Modifier.size(20.dp),
                        Ink,
                    )
                    if (hasUnreadNotifications) {
                        // Same unread marker as the site's bell: a primary dot with a white ring.
                        Box(
                            Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 9.dp)
                                .size(8.dp).background(Primary, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                        )
                    }
                }
            }
        }
    }
}
