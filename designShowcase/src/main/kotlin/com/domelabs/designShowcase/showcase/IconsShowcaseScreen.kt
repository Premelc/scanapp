package com.domelabs.designShowcase.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class IconShowcaseItem(
    val label: String,
    val icon: DrawableResource,
)

@Composable
fun IconsShowcaseScreen(onBack: () -> Unit) {
    ShowcaseDetailScreen(
        title = "Icons",
        onBack = onBack,
        preview = {
            scanAppIconSections().forEach { (sectionTitle, icons) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 88.dp),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false,
                    ) {
                        items(icons, key = { it.label }) { item ->
                            IconShowcaseTile(item)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun IconShowcaseTile(item: IconShowcaseItem) {
    NeoBrutalCard(showShadow = false) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.label,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun scanAppIconSections(): List<Pair<String, List<IconShowcaseItem>>> = listOf(
    "Scan" to listOf(
        IconShowcaseItem("QR", ScanAppTheme.Icons.qr),
        IconShowcaseItem("Barcode", ScanAppTheme.Icons.barcode),
        IconShowcaseItem("Gallery", ScanAppTheme.Icons.gallery),
        IconShowcaseItem("Flash on", ScanAppTheme.Icons.flashOn),
        IconShowcaseItem("Flash off", ScanAppTheme.Icons.flashOff),
        IconShowcaseItem("Zoom in", ScanAppTheme.Icons.zoomIn),
        IconShowcaseItem("Zoom out", ScanAppTheme.Icons.zoomOut),
    ),
    "Status" to listOf(
        IconShowcaseItem("Check", ScanAppTheme.Icons.checkCircle),
        IconShowcaseItem("Close", ScanAppTheme.Icons.closeCircleOutlined),
        IconShowcaseItem("Warning", ScanAppTheme.Icons.warning),
        IconShowcaseItem("Info", ScanAppTheme.Icons.info),
        IconShowcaseItem("Help", ScanAppTheme.Icons.help),
        IconShowcaseItem("Bug", ScanAppTheme.Icons.bug),
    ),
    "Navigation" to listOf(
        IconShowcaseItem("Home", ScanAppTheme.Icons.home),
        IconShowcaseItem("Menu", ScanAppTheme.Icons.menu),
        IconShowcaseItem("History", ScanAppTheme.Icons.history),
        IconShowcaseItem("Search", ScanAppTheme.Icons.search),
        IconShowcaseItem("Chevron down", ScanAppTheme.Icons.chevronDown),
        IconShowcaseItem("Chevron right", ScanAppTheme.Icons.chevronRight),
        IconShowcaseItem("Arrow left", ScanAppTheme.Icons.arrowLeft),
        IconShowcaseItem("Arrow down", ScanAppTheme.Icons.arrowDown),
    ),
    "Files & folders" to listOf(
        IconShowcaseItem("File", ScanAppTheme.Icons.file),
        IconShowcaseItem("Documents", ScanAppTheme.Icons.documents),
        IconShowcaseItem("Folder", ScanAppTheme.Icons.folder),
        IconShowcaseItem("Folder open", ScanAppTheme.Icons.folderOpen),
        IconShowcaseItem("Add folder", ScanAppTheme.Icons.addFolder),
        IconShowcaseItem("Folder delete", ScanAppTheme.Icons.folderDelete),
        IconShowcaseItem("Copy", ScanAppTheme.Icons.copy),
        IconShowcaseItem("Export", ScanAppTheme.Icons.export),
        IconShowcaseItem("Import", ScanAppTheme.Icons.import),
        IconShowcaseItem("Download", ScanAppTheme.Icons.download),
        IconShowcaseItem("Upload", ScanAppTheme.Icons.upload),
    ),
    "Actions" to listOf(
        IconShowcaseItem("Share", ScanAppTheme.Icons.share),
        IconShowcaseItem("Send", ScanAppTheme.Icons.send),
        IconShowcaseItem("Refresh", ScanAppTheme.Icons.refresh),
        IconShowcaseItem("Restart", ScanAppTheme.Icons.restart),
        IconShowcaseItem("Trash", ScanAppTheme.Icons.trash),
        IconShowcaseItem("Play", ScanAppTheme.Icons.play),
        IconShowcaseItem("Pause", ScanAppTheme.Icons.pause),
        IconShowcaseItem("Stop", ScanAppTheme.Icons.stop),
        IconShowcaseItem("Add", ScanAppTheme.Icons.addSquare),
        IconShowcaseItem("Remove", ScanAppTheme.Icons.minusSquare),
        IconShowcaseItem("More", ScanAppTheme.Icons.more),
        IconShowcaseItem("External", ScanAppTheme.Icons.external),
    ),
    "Account & security" to listOf(
        IconShowcaseItem("User", ScanAppTheme.Icons.user),
        IconShowcaseItem("User add", ScanAppTheme.Icons.userAdd),
        IconShowcaseItem("Identity", ScanAppTheme.Icons.identity),
        IconShowcaseItem("Login", ScanAppTheme.Icons.login),
        IconShowcaseItem("Lock", ScanAppTheme.Icons.lock),
        IconShowcaseItem("Key", ScanAppTheme.Icons.key),
        IconShowcaseItem("Fingerprint", ScanAppTheme.Icons.fingerprint),
        IconShowcaseItem("Eye open", ScanAppTheme.Icons.eyeOpen),
        IconShowcaseItem("Eye closed", ScanAppTheme.Icons.eyeClosed),
    ),
    "System & dev" to listOf(
        IconShowcaseItem("Gear", ScanAppTheme.Icons.gear),
        IconShowcaseItem("System", ScanAppTheme.Icons.system),
        IconShowcaseItem("Tool", ScanAppTheme.Icons.tool),
        IconShowcaseItem("Code", ScanAppTheme.Icons.code),
        IconShowcaseItem("Database", ScanAppTheme.Icons.database),
        IconShowcaseItem("Logs", ScanAppTheme.Icons.logs),
        IconShowcaseItem("Server", ScanAppTheme.Icons.server),
        IconShowcaseItem("Cloud", ScanAppTheme.Icons.cloud),
        IconShowcaseItem("Offline", ScanAppTheme.Icons.offline),
        IconShowcaseItem("Performance", ScanAppTheme.Icons.performance),
        IconShowcaseItem("Chart", ScanAppTheme.Icons.chart),
    ),
)
