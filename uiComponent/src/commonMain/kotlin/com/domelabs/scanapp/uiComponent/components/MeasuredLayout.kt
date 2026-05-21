package com.domelabs.scanapp.uiComponent.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MeasuredLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current.density
    var screenSize by remember { mutableStateOf(Metrics(0.dp, 0.dp)) }
    var orientation by remember(screenSize) {
        mutableStateOf(
            if (screenSize.width > screenSize.height) Orientation.LANDSCAPE else Orientation.PORTRAIT
        )
    }

    CompositionLocalProvider(
        LocalScreenMetrics provides screenSize,
        LocalOrientation provides orientation
    ) {
        Layout(
            modifier = modifier,
            content = content,
            measurePolicy = { measurables, constraints ->
                val width = constraints.maxWidth
                val height = constraints.maxHeight
                screenSize = Metrics(
                    width = (width / density).dp,
                    height = (height / density).dp,
                    widthPx = width,
                    heightPx = height,
                )

                val placeables = measurables.map { measurable ->
                    measurable.measure(constraints)
                }

                layout(width, height) {
                    var yPosition = 0
                    placeables.forEach { placeable ->
                        placeable.placeRelative(x = 0, y = yPosition)
                        yPosition += placeable.height
                    }
                }
            }
        )
    }
}

data class Metrics(
    val width: Dp,
    val height: Dp,
    val widthPx: Int = 0,
    val heightPx: Int = 0,
)

enum class Orientation {
    PORTRAIT,
    LANDSCAPE
}

val LocalScreenMetrics = staticCompositionLocalOf<Metrics> {
    error("No LocalScreenMetrics provided")
}

val LocalOrientation = staticCompositionLocalOf<Orientation> {
    error("No LocalScreenMetrics provided")
}
