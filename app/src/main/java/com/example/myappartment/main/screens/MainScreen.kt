package com.example.myappartment.main.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.main.navGraph.BottomBarScreen
import com.example.myappartment.main.navGraph.HomeNavGraph
import com.example.myappartment.navigateTo
import com.example.myappartment.viewModel.CityViewModel
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun MainScreen(navController: NavHostController = rememberNavController(), vm: UserViewModel, cityVm: CityViewModel, postVm: PostViewModel) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = it.calculateBottomPadding()
                )
        ) {
            HomeNavGraph(navController = navController, vm = vm, citiesVm = cityVm, postVm = postVm)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Feed,
        BottomBarScreen.Search,
        BottomBarScreen.Add,
        BottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route.route == currentDestination?.route }
    if (bottomBarDestination && currentDestination?.route != DestinationScreen.AddPost.route) {
        BottomNavigation {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ){uri ->
        uri?.let{
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.AddPost.createRoute(encoded)
            navController.navigate(route)
        }
    }
    BottomNavigationItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
                it.route == screen.route.route
        } == true,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            if(screen.title == "New post"){
                newPostImageLauncher.launch("image/*")
            } else{
                navigateTo(navController, screen.route)
            }
        }
    )
}