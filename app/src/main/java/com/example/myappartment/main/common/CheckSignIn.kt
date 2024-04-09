package com.example.myappartment.main.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.myappartment.Graph
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun CheckSignIn(vm: UserViewModel, navController: NavController) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(Graph.HOME)
//        ThemeState.darkModeState.value = vm.userData.value?.darkMode!!
//        navController.navigate(DestinationScreen.Feed.route) {
//            popUpTo(0)
//        }
    }
}