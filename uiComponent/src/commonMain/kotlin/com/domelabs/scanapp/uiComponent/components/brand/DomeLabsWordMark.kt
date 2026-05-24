package com.domelabs.scanapp.uiComponent.components.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import scan_app.uicomponent.generated.resources.Res
import scan_app.uicomponent.generated.resources.ic_dome_labs_wordmark_dark
import scan_app.uicomponent.generated.resources.ic_dome_labs_wordmark_light

@Composable
fun DomeLabsWordMark(
    modifier: Modifier = Modifier,
    size: WordMarkSize = WordMarkSize.REGULAR,
    isDark: Boolean = true,
) {
    val uriHandler: UriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.clickable {
            uriHandler.openUri("https://dome-labs.hr")
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleSmall.copy(color = if (isDark) Color.White else Color.Black),
            modifier = Modifier.padding(bottom = 4.dp),
            text = "Powered by"
        )
        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.width(
                when (size) {
                    WordMarkSize.REGULAR -> 115.dp
                    WordMarkSize.LARGE -> 200.dp
                }
            ),
            painter = if (isDark) {
                painterResource(Res.drawable.ic_dome_labs_wordmark_dark)
            } else {
                painterResource(
                    Res.drawable.ic_dome_labs_wordmark_light
                )
            },
            contentDescription = null,
        )
    }
}

enum class WordMarkSize {
    REGULAR,
    LARGE,
}
