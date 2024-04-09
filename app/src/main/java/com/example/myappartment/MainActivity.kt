package com.example.myappartment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myappartment.authentication.navGraph.authNavGraph
import com.example.myappartment.main.common.NotificationMessage
import com.example.myappartment.main.screens.*
import com.example.myappartment.ui.theme.MyAppartmentTheme
import com.example.myappartment.viewModel.CityViewModel
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isRestore = false

        if(intent.hasExtra("restore")){
            isRestore = intent.getBooleanExtra("restore", true)
        }
        Log.i("restore", isRestore.toString())
        setContent {
            MyAppartmentTheme {
                window?.setStatusBarColor(MaterialTheme.colors.primary.toArgb())
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(isRestore = isRestore)
                }
            }
        }
    }

    companion object {
        val filerWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "in", "it")
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
    object Chat : DestinationScreen("chat")

}

@Composable
fun MyApp(isRestore: Boolean) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val cityViewModel = hiltViewModel<CityViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()
    val navController = rememberNavController()

    NotificationMessage(userViewModel, cityViewModel, postViewModel) { vmm ->
        when (vmm) {
            is UserViewModel -> userViewModel.popupNotification
            is PostViewModel -> userViewModel.popupNotification
            is CityViewModel -> userViewModel.popupNotification
            else -> throw IllegalArgumentException("Unsupported ViewModel type")
        }
    }


    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navController, vm = userViewModel, isRestore = isRestore)
        composable(route = Graph.HOME) {
            MainScreen(vm = userViewModel, cityVm = cityViewModel, postVm = postViewModel)
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