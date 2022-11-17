package com.example.myappartment.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.*
import com.example.myappartment.main.common.*
import com.example.myappartment.ui.theme.LightPink
import com.example.myappartment.viewModel.AppViewModule


@Composable
fun ProfileScreen(navController: NavController, vm: AppViewModule) {

    val userData = vm.userData.value
    val isLoading = vm.inProgress.value
    val postsLoading = vm.refreshPostsProgress.value
    val posts = vm.posts.value

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
                    text = "${posts.size} posts",
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
                    Text(text = "Edit Profile", color = MaterialTheme.colors.onSecondary)
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
                noPostsMessage = "You haven't posted apartment yet"
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

