package com.example.myappartment.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.*
import com.example.myappartment.R
import com.example.myappartment.main.common.*
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel


@Composable
fun ProfileScreen(navController: NavController, vm: UserViewModel, postVm: PostViewModel) {

    val userData = vm.userData.value
    val isLoading = vm.inProgress.value
    var postsLoading = postVm.refreshPostsProgress.value
    var posts = postVm.posts.value

    LaunchedEffect(vm.userData.value, postVm.posts.value) {
        // Trigger actions when userData or posts change
        postsLoading = postVm.refreshPostsProgress.value
        posts = postVm.posts.value
    }

    Column {
        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(listOf(MaterialTheme.colors.primary, MaterialTheme.colors.background))),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage(userData?.imageUrl) {}
                Text(
                    text = if (posts.size==1) "${posts.size} post" else "${posts.size} posts",
                    textAlign = TextAlign.Center
                )
                val usernameDisplay =
                    if (userData?.username == null) "" else "@${userData.username}"
                val contactDisplay =
                    if (userData?.contactNumber == null) "" else "${userData.contactNumber}"
                val nameDisplay =
                    if (userData?.name == null) "" else "${userData.name}"
                val lastnameDisplay =
                    if (userData?.lastName == null) "" else "${userData.lastName}"
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = "$nameDisplay $lastnameDisplay",
                    fontWeight = FontWeight.Bold
                )
                Text(text = usernameDisplay)
                Text(text = contactDisplay)
                OutlinedButton(
                    onClick = { navController.navigate(Graph.PROFILE) },
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
                    Text(text = stringResource(R.string.editProfile), color = MaterialTheme.colors.onSecondary)
                }
            }
            PostList(
                vm = vm,
                isContextLoading = isLoading,
                postsLoading = postsLoading,
                posts = posts,
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
                    .fillMaxSize(),
                noPostsMessage = stringResource(R.string.noPostsPosted)
            ) { post ->
                vm.getUserById(post.userId)
                navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                navController.navigate(Graph.DETAILS)
            }
        }
//        BottomNavigationMenu(
//            selectedItem = BottomNavigationItem.PROFILE,
//            navController = navController
//        )
    }
    if (isLoading)
        ProgressSpinner()
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable { onClick.invoke() }
    ) {
        UserImageCard(
            userImage = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(80.dp)
        )
    }
}

