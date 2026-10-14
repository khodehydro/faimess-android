package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.theme.HeartRed
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Lime
import com.khodehydro.faimess.ui.theme.SoftSurface

@Composable
fun AppHeader(hasUnreadNotifications: Boolean, onCreate: () -> Unit, onNotifications: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).height(70.dp)
            .padding(start = 18.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(31.dp).background(Lime, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(R.drawable.ic_clover, modifier = Modifier.size(23.dp), tint = Ink)
        }
        Spacer(Modifier.width(6.dp))
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.9).sp,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(onClick = onCreate, modifier = Modifier.size(48.dp).testTag("create_post")) {
                Box(Modifier.size(40.dp).background(SoftSurface, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(R.drawable.ic_add_circle, stringResource(R.string.create_post), Modifier.size(19.dp), Ink)
                }
            }
            IconButton(onClick = onNotifications, modifier = Modifier.size(48.dp)) {
                Box(Modifier.size(40.dp).background(SoftSurface, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(
                        R.drawable.ic_bell,
                        stringResource(if (hasUnreadNotifications) R.string.unread_notifications else R.string.notifications),
                        Modifier.size(20.dp),
                        Ink,
                    )
                    if (hasUnreadNotifications) {
                        Box(Modifier.align(Alignment.TopEnd).padding(top = 9.dp, end = 10.dp)
                            .size(6.dp).background(HeartRed, CircleShape))
                    }
                }
            }
        }
    }
}
