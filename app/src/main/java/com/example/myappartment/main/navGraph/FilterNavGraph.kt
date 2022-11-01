package com.example.myappartment.main.navGraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.data.CityData
import com.example.myappartment.main.screens.FilterByCityScreen
import com.example.myappartment.viewModel.AppViewModule


fun NavGraphBuilder.filterNavGraph(navController: NavHostController, vm: AppViewModule) {
    navigation(
        route = Graph.FILTER,
        startDestination = DestinationScreen.CityFilter.route
    ) {
        composable(DestinationScreen.CityFilter.route) {
            val cityData =
                navController.previousBackStackEntry?.savedStateHandle?.get<CityData>("city")
            cityData?.let {
                FilterByCityScreen(navController = navController, vm = vm, city = cityData)
            }
        }
    }
}