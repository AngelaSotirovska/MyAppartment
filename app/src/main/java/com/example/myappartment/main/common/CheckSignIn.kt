package com.example.myappartment.main.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun CheckSignIn(vm: AppViewModule, navController: NavController) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route) {
            popUpTo(0)
        }
    }
}