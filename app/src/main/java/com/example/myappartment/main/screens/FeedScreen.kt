package com.example.myappartment.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.NavParam
import com.example.myappartment.R
import com.example.myappartment.main.common.BottomNavigationItem
import com.example.myappartment.main.common.BottomNavigationMenu
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.PostList
import com.example.myappartment.navigateTo
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun FeedScreen(navController: NavController, vm: AppViewModule) {

    val allPostsLoading = vm.allPostsProgress.value
    val allPosts = vm.allPosts.value

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo4), modifier = Modifier
                    .width(85.dp)
                    .height(70.dp), contentDescription = null
            )
        }
        LineDivider()
        PostList(
            vm = vm,
            isContextLoading = false,
            postsLoading = allPostsLoading,
            posts = allPosts,
            modifier = Modifier
                .weight(1f)
                .padding(1.dp)
                .fillMaxSize(),
            noPostsMessage = "No posts available at the moment"
        ) { post ->
            vm.getUserById(post.userId)
            navigateTo(navController, DestinationScreen.SinglePost, NavParam("post", post))
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.FEED,
            navController = navController
        )
    }
}