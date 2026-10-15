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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary

/**
 * Destinations keep a Material icon pair: the selected tab shows the filled glyph, every other
 * tab the outlined one. Search is a destination too, but it is drawn in its own circle beside
 * the menu capsule rather than inside it.
 */
enum class FeedTab(
    @StringRes val label: Int,
    @DrawableRes val outlinedIcon: Int,
    @DrawableRes val filledIcon: Int,
) {
    Home(R.string.home, R.drawable.ic_home_outline, R.drawable.ic_home_filled),
    Discover(R.string.discover, R.drawable.ic_explore_outline, R.drawable.ic_explore_filled),
    Calendar(R.string.calendar, R.drawable.ic_calendar_outline, R.drawable.ic_calendar_filled),
    Profile(R.string.profile, R.drawable.ic_person_outline, R.drawable.ic_person_filled),
    Search(R.string.search, R.drawable.ic_search, R.drawable.ic_search);

    val inMenu: Boolean get() = this != Search

    companion object {
        val menu: List<FeedTab> = entries.filter { it.inMenu }
    }
}

private val TabSize = 48.dp
private val ShellPadding = 7.dp
private val ShellHeight = TabSize + ShellPadding * 2

@Composable
fun FloatingNavigation(selected: FeedTab, onSelect: (FeedTab) -> Unit, modifier: Modifier = Modifier) {
    // The site's bottom menu: a translucent white capsule with a wide, faint, untinted shadow;
    // the active destination is the only thing painted in the brand purple. Search sits in a
    // separate circle of the same height, right beside the capsule.
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.weight(1f, fill = false).widthIn(max = 236.dp).fillMaxWidth()
                .navigationShell(RoundedCornerShape(50)).selectableGroup().padding(ShellPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FeedTab.menu.forEach { tab -> TabButton(tab, isSelected = selected == tab, onSelect) }
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(ShellHeight).navigationShell(CircleShape).padding(ShellPadding),
            contentAlignment = Alignment.Center,
        ) {
            TabButton(FeedTab.Search, isSelected = selected == FeedTab.Search, onSelect)
        }
    }
}

@Composable
private fun TabButton(tab: FeedTab, isSelected: Boolean, onSelect: (FeedTab) -> Unit) {
    val background by animateColorAsState(
        if (isSelected) Primary else Color.Transparent,
        animationSpec = tween(durationMillis = 90, easing = LinearEasing),
        label = "tabColor",
    )
    val label = stringResource(tab.label)
    Box(
        modifier = Modifier.size(TabSize).clip(CircleShape).background(background)
            .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(tab) })
            .semantics { contentDescription = label }.testTag("nav_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            if (isSelected) tab.filledIcon else tab.outlinedIcon,
            modifier = Modifier.size(22.dp),
            tint = if (isSelected) Color.White else InkMuted,
        )
    }
}

private fun Modifier.navigationShell(shape: Shape): Modifier = this
    .shadow(18.dp, shape, ambientColor = Ink.copy(alpha = 0.16f), spotColor = Ink.copy(alpha = 0.22f))
    .clip(shape)
    .background(Brush.verticalGradient(listOf(Color(0xF2FFFFFF), Color(0xE6FFFFFF))))
    .border(1.dp, Line, shape)
