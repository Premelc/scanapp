package com.domelabs.scanapp.uiComponent.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LineTabs(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onTabSelected: (Int) -> Unit,
    content: (@Composable (Int) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            val indicatorHeight by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(index) },
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier,
                    text = tab,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(
                            0.6f
                        )
                    },
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(indicatorHeight)
                        .fillMaxWidth(0.8f)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                )
            }
        }
    }

    content?.let { it1 ->
        Spacer(modifier = Modifier.height(8.dp))
        LineTabsContent(
            modifier = Modifier,
            selectedIndex = selectedIndex
        ) {
            it1(it)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LineTabsContent(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    content: @Composable (Int) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = selectedIndex,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -(width * 1.5).toInt() } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> (width * 1.5).toInt() } + fadeOut()
            }.using(SizeTransform(clip = false))
        }
    ) { targetIndex ->
        content(targetIndex)
    }
}
