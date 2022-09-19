package com.example.myappartment.main.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun NotificationMessage(vm: AppViewModule) {
    val notificationState = vm.popupNotification.value
    val notificationMessage = notificationState?.getContentOrNull()
    if (notificationMessage != null) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_LONG).show()
    }
}