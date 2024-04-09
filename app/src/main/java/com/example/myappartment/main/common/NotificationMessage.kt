package com.example.myappartment.main.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.myappartment.Event

@Composable
inline fun <reified T : ViewModel> NotificationMessage(vararg viewModels: T, crossinline notificationExtractor: (ViewModel) -> MutableState<Event<String>?>) {
    viewModels.forEach { vm ->
        val notificationState = notificationExtractor(vm).value
        val notificationMessage = notificationState?.getContentOrNull()
        if (notificationMessage != null) {
            Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_LONG).show()
        }
    }
}