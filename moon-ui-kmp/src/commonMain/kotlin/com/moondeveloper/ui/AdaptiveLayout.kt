package com.moondeveloper.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Constrains content to a max width and centers it horizontally.
 * Useful for preventing content from stretching too wide on tablets.
 */
@Composable
fun AdaptiveContentWidth(
    maxWidth: Dp = 600.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(modifier = Modifier.widthIn(max = maxWidth).fillMaxWidth()) {
            content()
        }
    }
}

/**
 * Two-pane layout with a vertical divider.
 * Used for side-by-side content on tablets (input/result, list/detail).
 */
@Composable
fun TwoPane(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    splitRatio: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(splitRatio)) { first() }
        VerticalDivider()
        Box(modifier = Modifier.weight(1f - splitRatio)) { second() }
    }
}

/**
 * List-Detail layout variant of TwoPane with 40/60 split.
 * Used for History list + detail pattern on tablets.
 */
@Composable
fun ListDetailLayout(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    splitRatio: Float = 0.4f,
    modifier: Modifier = Modifier
) {
    TwoPane(
        first = list,
        second = detail,
        splitRatio = splitRatio,
        modifier = modifier
    )
}
