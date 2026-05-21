@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.domelabs.scanapp.core.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject

@Composable
actual fun PermissionHandler(
    key: Int,
    permissionType: PermissionType,
    onPermissionStateChange: (PermissionState) -> Unit,
) {
    LaunchedEffect(key, permissionType) {
        when (permissionType) {
            PermissionType.CAMERA -> {
                val status = AVCaptureDevice.authorizationStatusForMediaType(
                    AVMediaTypeVideo
                )

                when (status) {
                    AVAuthorizationStatusAuthorized ->
                        onPermissionStateChange(PermissionState.GRANTED)

                    AVAuthorizationStatusNotDetermined ->
                        AVCaptureDevice.requestAccessForMediaType(
                            AVMediaTypeVideo
                        ) { granted ->
                            onPermissionStateChange(
                                if (granted) {
                                    PermissionState.GRANTED
                                } else {
                                    PermissionState.DENIED
                                }
                            )
                        }

                    else ->
                        onPermissionStateChange(PermissionState.DENIED)
                }
            }

            PermissionType.GALLERY -> {
                PHPhotoLibrary.requestAuthorization { status ->
                    onPermissionStateChange(
                        when (status) {
                            PHAuthorizationStatusAuthorized,
                            PHAuthorizationStatusLimited,
                                ->
                                PermissionState.GRANTED

                            PHAuthorizationStatusNotDetermined ->
                                PermissionState.SHOW_RATIONALE

                            else ->
                                PermissionState.DENIED
                        }
                    )
                }
            }

            PermissionType.CONTACT -> {
                val store = CNContactStore()
                store.requestAccessForEntityType(
                    CNEntityType.CNEntityTypeContacts
                ) { granted, _ ->
                    onPermissionStateChange(
                        if (granted) {
                            PermissionState.GRANTED
                        } else {
                            PermissionState.DENIED
                        }
                    )
                }
            }

            PermissionType.NOTIFICATION -> {
                UNUserNotificationCenter.currentNotificationCenter()
                    .requestAuthorizationWithOptions(
                        UNAuthorizationOptionAlert or
                                UNAuthorizationOptionSound or
                                UNAuthorizationOptionBadge
                    ) { granted, _ ->
                        onPermissionStateChange(
                            if (granted) {
                                PermissionState.GRANTED
                            } else {
                                PermissionState.DENIED
                            }
                        )
                    }
            }

            PermissionType.LOCATION -> {
                val locationManager = CLLocationManager()

                val status = CLLocationManager.authorizationStatus()
                CLAuthorizationStatus
                when (status.toLong()) { // convert to Long for easier comparison
                    3L, 4L -> onPermissionStateChange(
                        PermissionState.GRANTED
                    ) // AuthorizedAlways or AuthorizedWhenInUse
                    0L -> {
                        val locationManager = CLLocationManager()
                        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                            override fun locationManager(
                                manager: CLLocationManager,
                                didChangeAuthorizationStatus: CLAuthorizationStatus,
                            ) {
                                onPermissionStateChange(
                                    if (
                                        didChangeAuthorizationStatus.toLong() == 3L ||
                                        didChangeAuthorizationStatus.toLong() == 4L
                                    ) {
                                        PermissionState.GRANTED
                                    } else {
                                        PermissionState.DENIED
                                    }
                                )
                            }
                        }

                        locationManager.delegate = delegate
                        locationManager.requestWhenInUseAuthorization()
                    }

                    else -> onPermissionStateChange(PermissionState.DENIED) // Denied or Restricted
                }
            }
        }
    }
}

@Composable
actual fun CheckPermission(
    permissionType: PermissionType,
    update: (Boolean) -> Unit,
) {
    LaunchedEffect(permissionType) {
        val granted = when (permissionType) {
            PermissionType.CAMERA ->
                AVCaptureDevice.authorizationStatusForMediaType(
                    AVMediaTypeVideo
                ) == AVAuthorizationStatusAuthorized

            PermissionType.GALLERY -> {
                val status = PHPhotoLibrary.authorizationStatus()
                status == PHAuthorizationStatusAuthorized ||
                        status == PHAuthorizationStatusLimited
            }

            PermissionType.CONTACT ->
                CNContactStore.authorizationStatusForEntityType(
                    CNEntityType.CNEntityTypeContacts
                ) == CNAuthorizationStatusAuthorized

            PermissionType.NOTIFICATION -> false // iOS does not expose sync check
            PermissionType.LOCATION -> {
                val status = CLLocationManager.authorizationStatus()
                val granted = status.toLong() == 3L || status.toLong() == 4L
                update(granted)
                granted
            }
        }

        update(granted)
    }
}
