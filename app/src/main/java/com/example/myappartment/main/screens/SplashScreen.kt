package com.example.myappartment.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.R
import com.example.myappartment.navigateTo
import com.example.myappartment.ui.theme.LightPink
import com.example.myappartment.viewModel.AppViewModule
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController, vm: AppViewModule) {

    LaunchedEffect(key1 = true) {
        delay(3000)
        navigateTo(navController, DestinationScreen.Signup)
    }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .background(color = LightPink)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            modifier = Modifier
                .width(200.dp)
                .height(350.dp),
            contentDescription = null
        )
    }
}