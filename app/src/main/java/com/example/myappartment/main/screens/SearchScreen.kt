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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myappartment.Graph
import com.example.myappartment.R
import com.example.myappartment.main.common.*
import com.example.myappartment.viewModel.CityViewModel
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun SearchScreen(navController: NavController, vm: UserViewModel, citiesVm: CityViewModel, postVm: PostViewModel) {
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val searchedPosts = postVm.searchPosts.value
    val searchedPostsLoading = postVm.searchedPostsProgress.value
    val cities = citiesVm.cities.value

    Column {
        SearchBar(searchTerm, { searchTerm = it }, { postVm.searchPosts(searchTerm) })
        CitiesList(
            vm = vm,
            isContextLoading = false,
            citiesLoading = false,
            cities = cities,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) { city ->
//            navigateTo(
//                navController = navController,
//                destination = DestinationScreen.CityFilter,
//                NavParam("city", city)
//            )
            postVm.getPostsByCity(city.name)
            navController.currentBackStackEntry?.savedStateHandle?.set("city", city)
            navController.navigate(Graph.FILTER)
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
            noPostsMessage = stringResource(R.string.searchApartments)
        ) { post ->
            vm.getUserById(post.userId)
//            navigateTo(
//                navController = navController,
//                destination = DestinationScreen.SinglePost,
//                NavParam("post", post)
//            )
            navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
            navController.navigate(Graph.DETAILS)
        }
//        BottomNavigationMenu(
//            selectedItem = BottomNavigationItem.SEARCH,
//            navController = navController
//        )
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
            textColor = MaterialTheme.colors.onSecondary,
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

