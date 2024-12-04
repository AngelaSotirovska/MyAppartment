package com.example.myappartment.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.data.Message
import com.example.myappartment.main.common.Conversation
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.viewModel.UserViewModel



@Composable
fun ChatScreen(navController: NavController, vm: UserViewModel, otherUserId: String) {
    val scrollState = rememberScrollState()
    var message by remember { mutableStateOf(TextFieldValue("")) }
    val messages = vm.conversationMessages.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.popBackStack() })
            }
            LineDivider()
            Conversation(messages = messages, vm = vm)
            Spacer(modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {
            BasicTextField(
                value = message,
                onValueChange = { newValue -> message = newValue },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        // Handle sending message
                        println("Message Sent: ${message.text}")
                        message = TextFieldValue("") // Clear the input field after sending
                    }
                ),
                decorationBox = { innerTextField ->
                    if (message.text.isEmpty()) {
                        Text(text = "Type a message", color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    println("Message Sent: ${message.text}")
                    vm.sendMessage(Message(message.text, vm.userData.value?.userId, otherUserId, System.currentTimeMillis()))
                    message = TextFieldValue("")
                },
                enabled = message.text.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}