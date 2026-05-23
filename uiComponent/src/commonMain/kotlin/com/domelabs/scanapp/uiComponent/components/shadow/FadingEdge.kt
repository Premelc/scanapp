package com.domelabs.scanapp.uiComponent.components.shadow

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.fadingStart(
    fadingWidth: Dp = 50.dp,
    fadeToColor: Color = Color.Black,
    condition: () -> Boolean = { true },
) = drawWithContent {
    if (condition()) {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(
                bounds = size.toRect(),
                paint = Paint(),
            ) {
                drawContent()
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, fadeToColor),
                        startX = layoutDirection.getFadingStartStartX(size.width),
                        endX = layoutDirection.getFadingStartEndX(size.width, fadingWidth.toPx()),
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
        }
    } else {
        drawContent()
    }
}

fun Modifier.fadingEnd(
    fadingWidth: Dp = 50.dp,
    fadeToColor: Color = Color.Black,
    condition: () -> Boolean = { true },
) = drawWithContent {
    if (condition()) {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(
                bounds = size.toRect(),
                paint = Paint(),
            ) {
                drawContent()
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(fadeToColor, Color.Transparent),
                        startX = layoutDirection.getFadingEndStartX(size.width, fadingWidth.toPx()),
                        endX = layoutDirection.getFadingEndEndX(size.width),
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
        }
    } else {
        drawContent()
    }
}

fun Modifier.fadingTop(
    alpha: Float,
    fadingHeight: Dp = 8.dp,
    fadeToColor: Color = Color.Black,
    condition: () -> Boolean = { true },
) = drawWithContent {
    if (condition()) {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(
                bounds = size.toRect(),
                paint = Paint(),
            ) {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(fadeToColor.copy(alpha = alpha), Color.Transparent),
                        startY = 0f,
                        endY = fadingHeight.toPx(),
                    ),
                )
            }
        }
    } else {
        drawContent()
    }
}

fun Modifier.fadingBottom(
    alpha: Float,
    fadingHeight: Dp = 8.dp,
    fadeToColor: Color = Color.Black,
    condition: () -> Boolean = { true },
) = drawWithContent {
    if (condition()) {
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(
                bounds = size.toRect(),
                paint = Paint(),
            ) {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, fadeToColor.copy(alpha = alpha)),
                        startY = size.height - fadingHeight.toPx(),
                        endY = size.height,
                    ),
                )
            }
        }
    } else {
        drawContent()
    }
}

private fun LayoutDirection.getFadingEndStartX(rectWidth: Float, fadingWidth: Float) =
    if (this == LayoutDirection.Ltr) rectWidth - fadingWidth else fadingWidth

private fun LayoutDirection.getFadingEndEndX(rectWidth: Float) =
    if (this == LayoutDirection.Ltr) rectWidth else 0f

private fun LayoutDirection.getFadingStartStartX(rectWidth: Float) =
    if (this == LayoutDirection.Ltr) 0f else rectWidth

private fun LayoutDirection.getFadingStartEndX(rectWidth: Float, fadingWidth: Float) =
    if (this == LayoutDirection.Ltr) fadingWidth else rectWidth - fadingWidth

inline fun Modifier.thenIf(predicate: Boolean, modifierBuilder: Modifier.() -> Modifier) =
    if (predicate) then(modifierBuilder()) else this