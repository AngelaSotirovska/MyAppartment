package com.example.myappartment

import android.os.Bundle
import android.os.Parcelable
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
import com.example.myappartment.auth.screens.EditProfileScreen
import com.example.myappartment.auth.screens.LoginScreen
import com.example.myappartment.auth.screens.SignupScreen
import com.example.myappartment.data.CityData
import com.example.myappartment.data.PostData
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
        setContent {
            MyAppartmentTheme {
                window?.setStatusBarColor(LightPink.toArgb())
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MyApp()
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
fun MyApp() {
    val vm = hiltViewModel<AppViewModule>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm)

    NavHost(navController = navController, startDestination = DestinationScreen.Splash.route) {
        composable(DestinationScreen.Splash.route) {
            SplashScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Feed.route) {
            FeedScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Search.route) {
            SearchScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.AddPost.route) { navBackStackEntry ->
            val imageUri = navBackStackEntry.arguments?.getString("imageUri")
            imageUri?.let {
                AddNewPost(navController = navController, vm = vm, imageUri = it, post = null)
            }
        }
        composable(DestinationScreen.EditPost.route) {
            val postData =
                navController.previousBackStackEntry?.arguments?.getParcelable<PostData>("post")
            postData?.let {
                AddNewPost(navController = navController, vm = vm, imageUri = null, post = postData)
            }
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.EditProfile.route) {
            EditProfileScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.SinglePost.route) {
            val postData =
                navController.previousBackStackEntry?.arguments?.getParcelable<PostData>("post")
            postData?.let {
                SinglePostScreen(navController = navController, vm = vm, post = postData)
            }
        }
        composable(DestinationScreen.CityFilter.route) {
            val cityData =
                navController.previousBackStackEntry?.arguments?.getParcelable<CityData>("city")
            cityData?.let {
                FilterByCityScreen(navController = navController, vm = vm, city = cityData)
            }
        }
        composable(DestinationScreen.OpenImage.route) {
            val postImageUrl =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("imageUrl")
            postImageUrl?.let {
                ImageScreen(navController = navController, imageUrl = postImageUrl)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppartmentTheme {
        MyApp()
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