package com.example.myappartment.main.navGraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.authentication.screens.EditProfileScreen
import com.example.myappartment.viewModel.UserViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavHostController, vm: UserViewModel) {
    navigation(
        route = Graph.PROFILE,
        startDestination = DestinationScreen.EditProfile.route
    ) {
        composable(DestinationScreen.EditProfile.route) {
            EditProfileScreen(navController = navController, vm = vm)
        }
    }
}