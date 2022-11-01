package com.example.myappartment.main.navGraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.authentication.navGraph.authNavGraph
import com.example.myappartment.main.screens.AddNewPost
import com.example.myappartment.main.screens.FeedScreen
import com.example.myappartment.main.screens.ProfileScreen
import com.example.myappartment.main.screens.SearchScreen
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun HomeNavGraph(navController: NavController, vm: AppViewModule) {
    NavHost(
        navController as NavHostController,
        route = Graph.HOME,
        startDestination = DestinationScreen.Feed.route
    ) {
        composable(DestinationScreen.Feed.route) {
            FeedScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Search.route) {
            SearchScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.AddPost.route) { navBackStackEntry ->
            val imageUri = navBackStackEntry.arguments?.getString("imageUri")
            imageUri?.let {
                AddNewPost(navController = navController, vm = vm, imageUri = it, post = null)
            }
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, vm = vm)
        }
        detailsNavGraph(navController, vm)
        filterNavGraph(navController, vm)
        profileNavGraph(navController, vm)
        authNavGraph(navController, vm)
    }
}