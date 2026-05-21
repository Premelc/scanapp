package com.domelabs.scanapp.core.permission

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun PermissionHandler(
    key: Int,
    permissionType: PermissionType,
    onPermissionStateChange: (PermissionState) -> Unit,
) {
    PermissionHandler2(
        key = key,
        permission = permissionType,
        onPermissionStateChange = onPermissionStateChange
    )
}

private fun PermissionType.toPermission() = when (this) {
    PermissionType.CONTACT -> listOf(Manifest.permission.READ_CONTACTS)
    PermissionType.NOTIFICATION -> listOf(Manifest.permission.POST_NOTIFICATIONS)
    PermissionType.CAMERA -> listOf(Manifest.permission.CAMERA)
    PermissionType.GALLERY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    PermissionType.LOCATION -> listOf(
        ACCESS_COARSE_LOCATION,
        ACCESS_FINE_LOCATION
    )
}

private fun String.toPermissionType() = when (this) {
    Manifest.permission.READ_CONTACTS -> PermissionType.CONTACT
    Manifest.permission.POST_NOTIFICATIONS -> PermissionType.NOTIFICATION
    Manifest.permission.CAMERA -> PermissionType.CAMERA
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_VIDEO,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    -> PermissionType.GALLERY

    ACCESS_COARSE_LOCATION,
    ACCESS_FINE_LOCATION,
    -> PermissionType.LOCATION

    else -> null
}

@Composable
private fun PermissionHandler2(
    key: Int,
    permission: PermissionType,
    onPermissionStateChange: (PermissionState) -> Unit,
) {
    val context = LocalContext.current
    var numberOfTimesAsked by remember { mutableIntStateOf(0) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { results ->
            results.forEach { (_, value) ->
                if (value) {
                    numberOfTimesAsked = 0
                    PermissionDispatcher.updatePermissionResult(
                        permission = permission,
                        state = PermissionState.GRANTED
                    )
                    onPermissionStateChange(PermissionState.GRANTED)
                } else if (numberOfTimesAsked < 2) {
                    numberOfTimesAsked++
                    PermissionDispatcher.updatePermissionResult(
                        permission = permission,
                        state = PermissionState.SHOW_RATIONALE
                    )
                    onPermissionStateChange(PermissionState.SHOW_RATIONALE)
                } else {
                    PermissionDispatcher.updatePermissionResult(
                        permission = permission,
                        state = PermissionState.DENIED
                    )
                    onPermissionStateChange(PermissionState.DENIED)
                }
            }
        }
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (context.arePermissionsGranted(*permission.toPermission().toTypedArray())) {
            onPermissionStateChange(PermissionState.GRANTED)
        }
    }

    LaunchedEffect(key) {
        if (context.arePermissionsGranted(*permission.toPermission().toTypedArray())) {
            onPermissionStateChange(PermissionState.GRANTED)
        } else {
            if (numberOfTimesAsked >= 2) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                launcher.launch(intent)
            } else {
                requestPermissionLauncher.launch(permission.toPermission().toTypedArray())
            }
        }
    }
}

fun Context.arePermissionsGranted(vararg permissions: String): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

@Composable
actual fun CheckPermission(
    permissionType: PermissionType,
    update: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        update(context.arePermissionsGranted(*permissionType.toPermission().toTypedArray()))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                update(context.arePermissionsGranted(*permissionType.toPermission().toTypedArray()))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
