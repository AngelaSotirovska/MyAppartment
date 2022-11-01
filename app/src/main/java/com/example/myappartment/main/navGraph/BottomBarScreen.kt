package com.example.myappartment.main.navGraph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myappartment.DestinationScreen


sealed class BottomBarScreen(
    val route: DestinationScreen,
    val title: String,
    val icon: ImageVector
) {
    object Feed : BottomBarScreen(
        route = DestinationScreen.Feed,
        title = "Home",
        icon = Icons.Default.Home
    )

    object Search : BottomBarScreen(
        route = DestinationScreen.Search,
        title = "Search",
        icon = Icons.Default.Search
    )

    object Add : BottomBarScreen(
        route = DestinationScreen.AddPost,
        title = "New post",
        icon = Icons.Default.Add
    )

    object Profile : BottomBarScreen(
        route = DestinationScreen.Profile,
        title = "Account",
        icon = Icons.Default.Person
    )
}