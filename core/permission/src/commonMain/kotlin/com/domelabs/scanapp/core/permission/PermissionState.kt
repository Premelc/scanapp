package com.domelabs.scanapp.core.permission

enum class PermissionState {
    GRANTED,
    SHOW_RATIONALE,
    DENIED,
}

enum class PermissionType {
    CONTACT,
    NOTIFICATION,
    GALLERY,
    CAMERA,
    LOCATION,
}
