package com.example.myappartment.main.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myappartment.Graph
import com.example.myappartment.data.CityData
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.PostList
import com.example.myappartment.viewModel.UserViewModel
import com.example.myappartment.R
import com.example.myappartment.viewModel.PostViewModel

@Composable
fun FilterByCityScreen(navController: NavController, vm: UserViewModel, city: CityData, postVm: PostViewModel) {

    city.let {

        val filterPosts = postVm.filterPosts.value
        val filterPostsLoading = postVm.filterPostsLoading.value
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { navController.popBackStack() })
            }
            LineDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${city.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Filled.Place, contentDescription = null)
            }
            PostList(
                vm = vm,
                isContextLoading = false,
                postsLoading = filterPostsLoading,
                posts = filterPosts,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                noPostsMessage = stringResource(R.string.noPostsAvailableForCity, "${city.name}")
            ) { post ->
                vm.getUserById(post.userId)
//                navigateTo(
//                    navController = navController,
//                    destination = DestinationScreen.SinglePost,
//                    NavParam("post", post)
//                )
                navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                navController.navigate(Graph.DETAILS)
            }

        }
    }

}