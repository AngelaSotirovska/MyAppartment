package com.example.myappartment.main.navGraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.data.PostData
import com.example.myappartment.main.screens.AddNewPost
import com.example.myappartment.main.screens.ImageScreen
import com.example.myappartment.main.screens.SinglePostScreen
import com.example.myappartment.viewModel.AppViewModule


fun NavGraphBuilder.detailsNavGraph(navController: NavHostController, vm: AppViewModule) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DestinationScreen.SinglePost.route
    ) {
        composable(DestinationScreen.SinglePost.route) {
            val postData =
                navController.previousBackStackEntry?.savedStateHandle?.get<PostData>("post")
            postData?.let {
                SinglePostScreen(navController = navController, vm = vm, post = postData)
            }
        }
        composable(DestinationScreen.EditPost.route) {
            val postData =
                navController.previousBackStackEntry?.savedStateHandle?.get<PostData>("post")
            postData?.let {
                AddNewPost(navController = navController, vm = vm, imageUri = null, post = postData)
            }
        }
        composable(DestinationScreen.OpenImage.route) {
            val postImageUrl =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("imageUrl")
            postImageUrl?.let {
                ImageScreen(navController = navController, imageUrl = postImageUrl)
            }
        }
    }
}