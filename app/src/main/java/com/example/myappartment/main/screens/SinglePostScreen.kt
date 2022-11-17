package com.example.myappartment.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import com.example.myappartment.viewModel.AppViewModule
import java.text.SimpleDateFormat
import java.util.*

val showSideSettings = mutableStateOf(false)

@Composable
fun SinglePostScreen(navController: NavController, vm: AppViewModule, post: PostData) {
    val userData = vm.getUserDataById.value
    val userDataLoading = vm.getUserDataByIdLoading.value
    if (userDataLoading)
        ProgressSpinner()
    post.userId?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
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
                if (post.userId == vm.userData.value?.userId) {
                    IconButton(onClick = {
                        showSideSettings.value = true
                    }) {
                        Icon(Icons.Filled.MoreVert, null)
                    }
                }
            }
            LineDivider()
            SinglePostDisplay(navController = navController, vm = vm, post = post, user = userData)
        }
        if (showSideSettings.value) {
            SideSettingsDropdown(navController, vm, post)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun SinglePostDisplay(
    navController: NavController,
    vm: AppViewModule,
    post: PostData,
    user: UserData?
) {
    val userData = vm.userData.value
    val date = post.time?.let { Date(it) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm").format(date)

    Column(modifier = Modifier.fillMaxHeight()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape, modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    ImageComposable(data = user?.imageUrl)
                }
                Text(text = user?.username ?: "")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = post.title ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
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
                    var roomsString = "rooms"
                    val superscript =
                        SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 10.sp)
                    if (post.rooms != null && post.rooms == 1) {
                        roomsString = "room"
                    }
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = if (post.rooms == null) "" else "${post.rooms} $roomsString"
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = buildAnnotatedString {
                            if (post.squareFootage == null) {
                                append("")
                            } else {
                                append("${post.squareFootage} m")
                                withStyle(superscript) {
                                    append("2")
                                }
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
                text = if (post.description == null) "" else "${post.description}",
                textAlign = TextAlign.Justify
            )
        }


//    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
//        Image(
//            painter = painterResource(id = R.drawable.ic_fav),
//            contentDescription = null,
//            modifier = Modifier.size(24.dp),
//            colorFilter = ColorFilter.tint(Color.Red)
//        )
//        Text(
//            text = "${post.likes?.size ?: 0} saved this post as favorite",
//            modifier = Modifier.padding(start = 2.dp)
//        )
//    }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = if (user?.contactNumber == null) "" else "Contact: ${user.contactNumber}")
            Text(text = "Created on: $dateFormat", modifier = Modifier.padding(top = 8.dp))
        }

    }
}

@Composable
fun SideSettingsDropdown(navController: NavController, vm: AppViewModule, post: PostData) {
    val items = listOf(
        "Delete",
        "Edit"
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
                        vm.deletePost(post)
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

