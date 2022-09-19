package com.example.myappartment.main.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myappartment.DestinationScreen
import com.example.myappartment.NavParam
import com.example.myappartment.main.common.*
import com.example.myappartment.navigateTo
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun SearchScreen(navController: NavController, vm: AppViewModule) {
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val searchedPosts = vm.searchPosts.value
    val searchedPostsLoading = vm.searchedPostsProgress.value
    val cities = vm.cities.value

    Column {
        SearchBar(searchTerm, { searchTerm = it }, { vm.searchPosts(searchTerm) })
        CitiesList(
            vm = vm,
            isContextLoading = false,
            citiesLoading = false,
            cities = cities,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) { city ->
            navigateTo(
                navController = navController,
                destination = DestinationScreen.CityFilter,
                NavParam("city", city)
            )
            vm.getPostsByCity(city.name)
        }
        PostList(
            vm = vm,
            isContextLoading = false,
            postsLoading = searchedPostsLoading,
            posts = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            noPostsMessage = "Search apartments"
        ) { post ->
            vm.getUserById(post.userId)
            navigateTo(
                navController = navController,
                destination = DestinationScreen.SinglePost,
                NavParam("post", post)
            )
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.SEARCH,
            navController = navController
        )
    }

}

@Composable
fun SearchBar(searchTerm: String, onSearchChange: (String) -> Unit, onSearch: () -> Unit) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchTerm,
        onValueChange = onSearchChange,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, CircleShape),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = {
                onSearch()
                focusManager.clearFocus()
            }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        }
    )
}

