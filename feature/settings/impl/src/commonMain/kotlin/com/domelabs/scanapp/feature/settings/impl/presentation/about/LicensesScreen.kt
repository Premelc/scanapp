package com.domelabs.scanapp.feature.settings.impl.presentation.about

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.utils.toClipEntry
import com.domelabs.scanapp.uiComponent.components.LineTabs
import com.domelabs.scanapp.uiComponent.components.ScreenTopBar
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun LicensesScreen() {
    val scope = rememberCoroutineScope()
    var selectedLineTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTopBar(
            title = "Licenses",
            onBack = {
                scope.launch {
                    NavigationDispatcher.back()
                }
            },
        )
        LineTabs(
            selectedIndex = selectedLineTab,
            onTabSelected = { selectedLineTab = it },
            tabs = listOf(
                "Attributions",
                "Libraries"
            )
        ) {
            when (it) {
                0 -> AttributionContent()
                else -> LibrariesContent()
            }
        }
    }
}

@Composable
private fun LibrariesContent() {
    val platformContext = koinInject<Any>(named("platformContext"))
    val libraries by produceLibraries {
        readAboutLibrariesDefinition(platformContext)
    }
    LibrariesContainer(
        modifier = Modifier,
        libraries = libraries,
        showVersion = false,
        typography = MaterialTheme.typography
    )
}

@Composable
private fun AttributionContent() {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Attributions",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        attributions.forEach { attribution ->
            Text(
                text = "• ${attribution.title}",
                style = MaterialTheme.typography.bodyMedium
            )
            attribution.link?.let {
                Text(
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            uriHandler.openUri(it)
                        },
                        onLongClick = {
                            scope.launch { clipboardManager.setClipEntry(it.toClipEntry()) }
                        }
                    ),
                    text = it,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            attribution.note?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private val attributions = listOf(
    Attribution(
        title = "Solar Icons",
        link = "https://www.svgrepo.com/author/Solar%20Icons/"
    ),
    Attribution(
        title = "SVG Repo",
        link = "https://www.svgrepo.com/page/licensing",
        note = "All icons taken from SVG Repo"
    ),
    Attribution(
        title = "App fonts provided by Google Fonts"
    )
)

data class Attribution(
    val title: String,
    val link: String? = null,
    val note: String? = null,
)
