package com.domelabs.scanapp.uiComponent.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import scan_app.uicomponent.generated.resources.Res
import scan_app.uicomponent.generated.resources.ic_add_folder
import scan_app.uicomponent.generated.resources.ic_add_square
import scan_app.uicomponent.generated.resources.ic_arrow_down
import scan_app.uicomponent.generated.resources.ic_arrow_left
import scan_app.uicomponent.generated.resources.ic_barcode
import scan_app.uicomponent.generated.resources.ic_bug
import scan_app.uicomponent.generated.resources.ic_chart
import scan_app.uicomponent.generated.resources.ic_check_circle
import scan_app.uicomponent.generated.resources.ic_chevron_down
import scan_app.uicomponent.generated.resources.ic_chevron_right
import scan_app.uicomponent.generated.resources.ic_close_circle_outlined
import scan_app.uicomponent.generated.resources.ic_cloud
import scan_app.uicomponent.generated.resources.ic_code
import scan_app.uicomponent.generated.resources.ic_copy
import scan_app.uicomponent.generated.resources.ic_database
import scan_app.uicomponent.generated.resources.ic_docker
import scan_app.uicomponent.generated.resources.ic_documents
import scan_app.uicomponent.generated.resources.ic_dome_labs_wordmark_dark
import scan_app.uicomponent.generated.resources.ic_dome_labs_wordmark_light
import scan_app.uicomponent.generated.resources.ic_download
import scan_app.uicomponent.generated.resources.ic_download_cloud
import scan_app.uicomponent.generated.resources.ic_export
import scan_app.uicomponent.generated.resources.ic_external
import scan_app.uicomponent.generated.resources.ic_eye_closed
import scan_app.uicomponent.generated.resources.ic_eye_open
import scan_app.uicomponent.generated.resources.ic_file
import scan_app.uicomponent.generated.resources.ic_fingerprint
import scan_app.uicomponent.generated.resources.ic_flash_off
import scan_app.uicomponent.generated.resources.ic_flash_on
import scan_app.uicomponent.generated.resources.ic_folder
import scan_app.uicomponent.generated.resources.ic_folder_delete
import scan_app.uicomponent.generated.resources.ic_folder_open
import scan_app.uicomponent.generated.resources.ic_gallery
import scan_app.uicomponent.generated.resources.ic_gear
import scan_app.uicomponent.generated.resources.ic_github
import scan_app.uicomponent.generated.resources.ic_google
import scan_app.uicomponent.generated.resources.ic_help
import scan_app.uicomponent.generated.resources.ic_history
import scan_app.uicomponent.generated.resources.ic_home
import scan_app.uicomponent.generated.resources.ic_identity
import scan_app.uicomponent.generated.resources.ic_import
import scan_app.uicomponent.generated.resources.ic_info
import scan_app.uicomponent.generated.resources.ic_key
import scan_app.uicomponent.generated.resources.ic_laptop
import scan_app.uicomponent.generated.resources.ic_lock
import scan_app.uicomponent.generated.resources.ic_login
import scan_app.uicomponent.generated.resources.ic_logs
import scan_app.uicomponent.generated.resources.ic_mail
import scan_app.uicomponent.generated.resources.ic_menu
import scan_app.uicomponent.generated.resources.ic_minus_square
import scan_app.uicomponent.generated.resources.ic_more
import scan_app.uicomponent.generated.resources.ic_nginx
import scan_app.uicomponent.generated.resources.ic_offline
import scan_app.uicomponent.generated.resources.ic_other
import scan_app.uicomponent.generated.resources.ic_pause
import scan_app.uicomponent.generated.resources.ic_performance
import scan_app.uicomponent.generated.resources.ic_play
import scan_app.uicomponent.generated.resources.ic_podman
import scan_app.uicomponent.generated.resources.ic_qr
import scan_app.uicomponent.generated.resources.ic_refresh
import scan_app.uicomponent.generated.resources.ic_restart
import scan_app.uicomponent.generated.resources.ic_router
import scan_app.uicomponent.generated.resources.ic_search
import scan_app.uicomponent.generated.resources.ic_send
import scan_app.uicomponent.generated.resources.ic_server
import scan_app.uicomponent.generated.resources.ic_share
import scan_app.uicomponent.generated.resources.ic_star
import scan_app.uicomponent.generated.resources.ic_stop
import scan_app.uicomponent.generated.resources.ic_swissh
import scan_app.uicomponent.generated.resources.ic_swissh_hub
import scan_app.uicomponent.generated.resources.ic_system
import scan_app.uicomponent.generated.resources.ic_tool
import scan_app.uicomponent.generated.resources.ic_trash
import scan_app.uicomponent.generated.resources.ic_upload
import scan_app.uicomponent.generated.resources.ic_upload_cloud
import scan_app.uicomponent.generated.resources.ic_user
import scan_app.uicomponent.generated.resources.ic_user_add
import scan_app.uicomponent.generated.resources.ic_vibrate
import scan_app.uicomponent.generated.resources.ic_warning
import scan_app.uicomponent.generated.resources.im_app

object ScanAppTheme {
    object Icons {
        val menu = Res.drawable.ic_menu
        val barcode = Res.drawable.ic_barcode
        val qr = Res.drawable.ic_qr
        val gallery = Res.drawable.ic_gallery
        val flashOn = Res.drawable.ic_flash_on
        val flashOff = Res.drawable.ic_flash_off
        val addFolder = Res.drawable.ic_add_folder
        val addSquare = Res.drawable.ic_add_square
        val arrowDown = Res.drawable.ic_arrow_down
        val arrowLeft = Res.drawable.ic_arrow_left
        val bug = Res.drawable.ic_bug
        val chart = Res.drawable.ic_chart
        val checkCircle = Res.drawable.ic_check_circle
        val chevronDown = Res.drawable.ic_chevron_down
        val chevronRight = Res.drawable.ic_chevron_right
        val closeCircleOutlined = Res.drawable.ic_close_circle_outlined
        val cloud = Res.drawable.ic_cloud
        val code = Res.drawable.ic_code
        val copy = Res.drawable.ic_copy
        val database = Res.drawable.ic_database
        val docker = Res.drawable.ic_docker
        val documents = Res.drawable.ic_documents
        val domeLabsWordmarkDark = Res.drawable.ic_dome_labs_wordmark_dark
        val domeLabsWordmarkLight = Res.drawable.ic_dome_labs_wordmark_light
        val download = Res.drawable.ic_download
        val downloadCloud = Res.drawable.ic_download_cloud
        val export = Res.drawable.ic_export
        val external = Res.drawable.ic_external
        val eyeClosed = Res.drawable.ic_eye_closed
        val eyeOpen = Res.drawable.ic_eye_open
        val file = Res.drawable.ic_file
        val fingerprint = Res.drawable.ic_fingerprint
        val folder = Res.drawable.ic_folder
        val folderDelete = Res.drawable.ic_folder_delete
        val folderOpen = Res.drawable.ic_folder_open
        val gear = Res.drawable.ic_gear
        val github = Res.drawable.ic_github
        val google = Res.drawable.ic_google
        val help = Res.drawable.ic_help
        val history = Res.drawable.ic_history
        val home = Res.drawable.ic_home
        val identity = Res.drawable.ic_identity
        val import = Res.drawable.ic_import
        val info = Res.drawable.ic_info
        val key = Res.drawable.ic_key
        val laptop = Res.drawable.ic_laptop
        val lock = Res.drawable.ic_lock
        val login = Res.drawable.ic_login
        val logs = Res.drawable.ic_logs
        val mail = Res.drawable.ic_mail
        val minusSquare = Res.drawable.ic_minus_square
        val more = Res.drawable.ic_more
        val nginx = Res.drawable.ic_nginx
        val offline = Res.drawable.ic_offline
        val other = Res.drawable.ic_other
        val pause = Res.drawable.ic_pause
        val performance = Res.drawable.ic_performance
        val play = Res.drawable.ic_play
        val podman = Res.drawable.ic_podman
        val refresh = Res.drawable.ic_refresh
        val restart = Res.drawable.ic_restart
        val router = Res.drawable.ic_router
        val search = Res.drawable.ic_search
        val send = Res.drawable.ic_send
        val server = Res.drawable.ic_server
        val share = Res.drawable.ic_share
        val star = Res.drawable.ic_star
        val stop = Res.drawable.ic_stop
        val swissh = Res.drawable.ic_swissh
        val swisshHub = Res.drawable.ic_swissh_hub
        val system = Res.drawable.ic_system
        val tool = Res.drawable.ic_tool
        val trash = Res.drawable.ic_trash
        val upload = Res.drawable.ic_upload
        val uploadCloud = Res.drawable.ic_upload_cloud
        val user = Res.drawable.ic_user
        val userAdd = Res.drawable.ic_user_add
        val vibrate = Res.drawable.ic_vibrate
        val warning = Res.drawable.ic_warning

        val app = Res.drawable.im_app
    }

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}
