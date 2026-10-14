package com.khodehydro.faimess.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Lime
import com.khodehydro.faimess.ui.theme.Olive

enum class FeedTab(@StringRes val label: Int, @DrawableRes val icon: Int) {
    Home(R.string.home, R.drawable.ic_home),
    Discover(R.string.discover, R.drawable.ic_gallery),
    Calendar(R.string.calendar, R.drawable.ic_calendar),
    Profile(R.string.profile, R.drawable.ic_person),
    Search(R.string.search, R.drawable.ic_search),
}

@Composable
fun FloatingNavigation(selected: FeedTab, onSelect: (FeedTab) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier.widthIn(max = 284.dp).fillMaxWidth()
            .shadow(18.dp, shape, ambientColor = Olive.copy(alpha = 0.18f), spotColor = Ink.copy(alpha = 0.22f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xCC98AB69), Color(0xE070843E))))
            .border(1.dp, Color.White.copy(alpha = 0.28f), shape)
            .selectableGroup().padding(7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedTab.entries.forEach { tab ->
            val isSelected = selected == tab
            val background by animateColorAsState(
                if (isSelected) Lime else Color.White.copy(alpha = 0.15f),
                animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                label = "tabColor",
            )
            val label = stringResource(tab.label)
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(background)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(tab) })
                    .semantics { contentDescription = label }.testTag("nav_${tab.name.lowercase()}"),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(tab.icon, modifier = Modifier.size(22.dp), tint = if (isSelected) Ink else Color.White)
            }
        }
    }
}
