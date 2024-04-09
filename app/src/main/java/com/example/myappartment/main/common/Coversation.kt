package com.example.myappartment.main.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun Conversation(messages: List<String>, vm: UserViewModel) {
    Column {
        messages.forEach { message ->
            MessageItem(msg = message, vm = vm)
            Spacer(modifier = Modifier.height(8.dp)) // Add some space between messages
        }
    }
}