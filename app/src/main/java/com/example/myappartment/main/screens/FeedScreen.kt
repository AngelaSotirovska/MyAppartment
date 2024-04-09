package com.example.myappartment.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.*
import com.example.myappartment.R
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.PostList
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun FeedScreen(navController: NavController, vm: UserViewModel, postVm: PostViewModel) {

//    val allPostsLoading = vm.allPostsProgress.value
//    val allPosts = vm.allPosts.value
    val allPostsLoading = postVm.allPostsProgress.value
    val allPosts = postVm.allPosts.value

    Box(modifier = Modifier.fillMaxSize(), ) {
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
                    painter = painterResource(id = if(ThemeState.darkModeState.value) R.drawable.ic_logo_dark else R.drawable.ic_logo_light), modifier = Modifier
                        .width(90.dp)
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
                    .padding(1.dp)
                    .fillMaxSize(),
                noPostsMessage = stringResource(R.string.noPostsAvailable)
            ) { post ->
                vm.getUserById(post.userId)
//                navigateTo(navController, DestinationScreen.SinglePost, NavParam("post", post))
                navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                navController.navigate(Graph.DETAILS)
            }
//        BottomNavigationMenu(
//            selectedItem = BottomNavigationItem.FEED,
//            navController = navController
//        )
        }
    }
}