package com.domelabs.scanapp.feature.settings.impl.presentation.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.utils.timeNow
import com.domelabs.scanapp.feature.settings.impl.platform.AppInfo
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.brand.DomeLabsWordMark
import com.domelabs.scanapp.uiComponent.components.brand.WordMarkSize
import com.domelabs.scanapp.uiComponent.theme.BrightCyan
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.PastelBlue
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Instant

private const val PRIVACY_POLICY_URL = "https://dome-labs.hr/privacy"
private const val TERMS_AND_SERVICE_URL = "https://dome-labs.hr/terms"

@Composable
fun AboutScreen(
    appInfo: AppInfo = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    AboutContent(
        versionName = appInfo.versionName,
        onBack = { scope.launch { NavigationDispatcher.back() } },
        onOpenPrivacyPolicy = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        onOpenTermsOfService = { uriHandler.openUri(TERMS_AND_SERVICE_URL) },
        onOpenLicenses = {
            scope.launch {
                NavigationDispatcher.navigate(NavRoute.Licenses)
            }
        },
    )
}

@Composable
internal fun AboutContent(
    versionName: String,
    onBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        NeoBrutalButton(
            text = "Back",
            style = NeoBrutalButtonStyle.Secondary,
            onClick = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            AppInfoSection(versionName = versionName)

            Spacer(modifier = Modifier.height(8.dp))

            LegalSection(
                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                onOpenTermsOfService = onOpenTermsOfService,
                onOpenLicenses = onOpenLicenses,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DomeLabsWordMark(
                    size = WordMarkSize.REGULAR,
                    isDark = false,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\u00A9 ${getCurrentYear()} Dome Labs. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppInfoSection(versionName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeoBrutalCard(
            modifier = Modifier,
            showShadow = true,
        ) {
            Image(
                painter = painterResource(ScanAppTheme.Icons.app),
                contentDescription = "QRScanr icon",
                modifier = Modifier.size(160.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "QRScanr",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version $versionName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A fast, modern QR and barcode scanner for everyday codes, payment slips, and more.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun LegalSection(
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Legal",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrightCyan,
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )

        NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                LegalLinkItem(
                    icon = ScanAppTheme.Icons.lock,
                    title = "Privacy Policy",
                    onClick = onOpenPrivacyPolicy,
                )
                HorizontalDivider(
                    color = NeoBlack.copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 56.dp),
                )
                LegalLinkItem(
                    icon = ScanAppTheme.Icons.documents,
                    title = "Terms of Service",
                    onClick = onOpenTermsOfService,
                )
                HorizontalDivider(
                    color = NeoBlack.copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 56.dp),
                )
                LegalLinkItem(
                    icon = ScanAppTheme.Icons.info,
                    title = "Licenses & attribution",
                    onClick = onOpenLicenses,
                )
            }
        }
    }
}

@Composable
private fun LegalLinkItem(
    icon: DrawableResource,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(ScanAppTheme.Icons.external),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun getCurrentYear(): String {
    return Instant.fromEpochMilliseconds(timeNow())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year
        .toString()
}
