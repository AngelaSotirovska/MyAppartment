package com.example.myappartment.authentication.navGraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.authentication.screens.LoginScreen
import com.example.myappartment.authentication.SignupScreen
import com.example.myappartment.main.screens.SplashScreen
import com.example.myappartment.viewModel.AppViewModule

fun NavGraphBuilder.authNavGraph(navController: NavController, vm: AppViewModule, isRestore: Boolean) {
    lateinit var startActivity: String
    if(isRestore) {
        startActivity = DestinationScreen.Login.route
    } else {
        startActivity = DestinationScreen.Splash.route
    }
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = startActivity
    ) {
        composable(DestinationScreen.Splash.route) {
            SplashScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController = navController, vm = vm)
        }
    }
}