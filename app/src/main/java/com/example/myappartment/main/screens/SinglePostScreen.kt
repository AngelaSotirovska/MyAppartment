package com.example.myappartment.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myappartment.*
import com.example.myappartment.R
import com.example.myappartment.data.PostData
import com.example.myappartment.data.UserData
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.ImageComposable
import com.example.myappartment.main.common.ProgressSpinner
import com.example.myappartment.main.common.UserImageCard
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

val showSideSettings = mutableStateOf(false)

@Composable
fun SinglePostScreen(navController: NavController, vm: UserViewModel, postVm: PostViewModel, post: PostData) {
    val userData = vm.getUserDataById.value
    val userDataLoading = vm.getUserDataByIdLoading.value
    post.postId?.let { postVm.getPostById(it) }

    val getPost = postVm.post

    if (userDataLoading)
        ProgressSpinner()
    post.let {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(8.dp)
//        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Back", fontWeight = FontWeight.Bold, modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.popBackStack() })

                if (getPost.value?.userId == vm.userData.value?.userId) {
                    IconButton(onClick = {
                        showSideSettings.value = true
                    }) {
                        Icon(Icons.Filled.MoreVert, null)
                    }
                }
            }
            LineDivider()
            getPost.value?.let { it1 ->
                SinglePostDisplay(
                    navController = navController,
                    vm = vm,
                    post = it1,
                    user = userData
                )
            }
        }
        if (showSideSettings.value) {
            SideSettingsDropdown(navController, vm, postVm, post)
        }
    }

}

@SuppressLint("SimpleDateFormat")
@Composable
fun SinglePostDisplay(
    navController: NavController,
    vm: UserViewModel,
    post: PostData,
    user: UserData?
) {
    val date = post.time?.let { Date(it) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm").format(date)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserImageCard(userImage = user?.imageUrl, modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp))
                Text(text = user?.username ?: "", Modifier.padding(start = 8.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 10.dp, start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = post.title ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Start,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    ImageComposable(data = post.postImage,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(220.dp)
                            .clip(
                                RoundedCornerShape(percent = 10)
                            )
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "imageUrl",
                                    value = post.postImage
                                )
                                navigateTo(
                                    navController = navController,
                                    destination = DestinationScreen.OpenImage
                                )
                            })

                    Image(
                        painter = painterResource(id = R.drawable.ic_zoom_in),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(60.dp)
                            .padding(5.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
                Column(modifier = Modifier.padding(15.dp)) {
                    val resources = LocalContext.current.resources
                    val rooms = post.rooms!!

                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = resources.getQuantityString(R.plurals.rooms_format, rooms, rooms)
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = buildAnnotatedString {
                            if (post.squareFootage == null) {
                                append("")
                            } else {
                                append(
                                    stringResource(
                                        R.string.squareMeter,
                                        "${post.squareFootage}"
                                    )
                                )
//                                append("${post.squareFootage} m")
//                                withStyle(superscript) {
//                                    append("2")
//                                }
                            }
                        }
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = if (post.price == null) "" else "${post.price} euros"
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
                .padding(8.dp)
        ) {
            Text(
                text = if (post.description == null) "" else "${post.description} ${user?.userId} ${post.userId}",
                textAlign = TextAlign.Justify
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = if (user?.contactNumber == null) "" else "Contact: ${user.contactNumber}")
            Text(text = "Created on: $dateFormat", modifier = Modifier.padding(top = 8.dp))
        }

        val userId1 = user?.userId?.toString()?.trim()
        val userId2 = post.userId?.toString()?.trim()

        if (userId1 != null && userId2 != null && userId1 != userId2) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("userId", post.userId)
                        navController.navigate(DestinationScreen.Chat.route)
                    },
                    modifier = Modifier
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    shape = RoundedCornerShape(10)
                )
                {
                    Text(text = stringResource(R.string.openChat), color = MaterialTheme.colors.onSecondary)
                }
            }
        }

    }
}

@Composable
fun SideSettingsDropdown(navController: NavController, vm: UserViewModel, postVm: PostViewModel, post: PostData) {
    val items = listOf(
        stringResource(R.string.delete),
        stringResource(R.string.edit)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .absolutePadding(top = 45.dp, right = 20.dp)
    ) {
        DropdownMenu(
            expanded = showSideSettings.value,
            onDismissRequest = { showSideSettings.value = false },
            modifier = Modifier
                .width(100.dp)
                .background(
                    MaterialTheme.colors.secondary
                )
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    showSideSettings.value = false
                    if (s == "Delete") {
                        navController.popBackStack()
                        postVm.deletePost(post)
                    } else if (s == "Edit") {
                        navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                        navController.navigate(DestinationScreen.EditPost.route)
                    }
                }) {
                    Text(text = s, color = MaterialTheme.colors.onSecondary)
                    if (index < 3) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

