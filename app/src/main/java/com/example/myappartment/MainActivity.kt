package com.example.myappartment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myappartment.authentication.navGraph.authNavGraph
import com.example.myappartment.main.common.NotificationMessage
import com.example.myappartment.main.screens.*
import com.example.myappartment.ui.theme.LightPink
import com.example.myappartment.ui.theme.MyAppartmentTheme
import com.example.myappartment.viewModel.AppViewModule
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isRestore: Boolean = false;
        if(intent.hasExtra("restore")){
            isRestore = intent.getBooleanExtra("restore", true)
        }
        Log.i("restore", isRestore.toString())
        setContent {
            MyAppartmentTheme {
                window?.setStatusBarColor(MaterialTheme.colors.primary.toArgb())
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(isRestore = isRestore)
                }
            }
        }
    }
}

sealed class DestinationScreen(val route: String) {
    object Signup : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Feed : DestinationScreen("feed")
    object Search : DestinationScreen("search")
    object AddPost : DestinationScreen("addpost/{imageUri}") {
        fun createRoute(uri: String) = "addpost/$uri"
    }

    object EditPost : DestinationScreen("editpost")

    object Profile : DestinationScreen("profile")
    object EditProfile : DestinationScreen("editProfile")
    object SinglePost : DestinationScreen("singlepost")
    object CityFilter : DestinationScreen("cityfilter")
    object Splash : DestinationScreen("splash")
    object OpenImage : DestinationScreen("image")
}

@Composable
fun MyApp(isRestore: Boolean) {
    val vm = hiltViewModel<AppViewModule>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm)

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navController, vm = vm, isRestore = isRestore)
        composable(route = Graph.HOME) {
            MainScreen(vm = vm)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph"
    const val FILTER = "filter_graph"
    const val PROFILE = "profile_graph"
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppartmentTheme {
        MyApp(false)
    }
}

data class NavParam(
    val name: String,
    val value: Parcelable
)

fun navigateTo(
    navController: NavController,
    destination: DestinationScreen,
    vararg params: NavParam
) {
    for (param in params) {
        navController.currentBackStackEntry?.arguments?.putParcelable(param.name, param.value)
    }
    navController.navigate(destination.route) {
        popUpTo(destination.route)
        launchSingleTop = true
    }
}