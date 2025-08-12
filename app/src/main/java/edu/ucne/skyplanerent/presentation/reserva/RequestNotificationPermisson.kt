package edu.ucne.skyplanerent.presentation.reserva

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionState.launchPermissionRequest()
        }
    }
}