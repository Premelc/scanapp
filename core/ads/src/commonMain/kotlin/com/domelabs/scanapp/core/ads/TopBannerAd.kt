package com.domelabs.scanapp.core.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A persistent banner ad anchored to the top of the screen.
 */
@Composable
expect fun TopBannerAd(modifier: Modifier = Modifier)
