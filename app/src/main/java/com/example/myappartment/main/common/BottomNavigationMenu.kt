package com.example.myappartment.main.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import androidx.navigation.NavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.R
import com.example.myappartment.navigateTo
import java.io.ByteArrayOutputStream


enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    FEED(R.drawable.ic_home, DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    ADDPOST(R.drawable.ic_add_box, DestinationScreen.AddPost),
    PROFILE(R.drawable.ic_profile, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {

    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ){uri ->
        uri?.let{
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.AddPost.createRoute(encoded)
            navController.navigate(route)
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.LightGray)
    ) {
        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp)
                    .weight(1f)
                    .clickable {
                        if (item.navDestination.route.contains("addpost/")) {
                            newPostImageLauncher.launch("image/*")
                        } else {
                            navigateTo(navController, item.navDestination)
                        }
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(Color.Black) else ColorFilter.tint(
                    Color.DarkGray
                )
            )
        }
    }
}

