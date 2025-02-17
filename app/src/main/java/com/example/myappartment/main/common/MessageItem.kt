package com.example.myappartment.main.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myappartment.R
import com.example.myappartment.viewModel.UserViewModel
import androidx.compose.ui.draw.shadow
import com.example.myappartment.data.Message

@Composable
fun MessageItem(msg: Message, vm: UserViewModel) {
    var userMessage = vm.userData.value
    var showRight = false
    LaunchedEffect(msg.senderId) {
        msg.senderId?.let {
            vm.getUser(it) { user ->
                if (user != null) {
                    userMessage = user
                    if(user.userId !== vm.userData.value?.userId) {
                        showRight = true
                    }
                }
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(all = 8.dp), horizontalArrangement = if (showRight) Arrangement.End else Arrangement.Start) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            userMessage?.name?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.caption
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
                    .shadow(1.dp, shape = MaterialTheme.shapes.medium)
            ) {
                msg.message?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

