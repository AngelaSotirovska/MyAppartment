package com.example.myappartment.main.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import androidx.compose.ui.geometry.Size;
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import com.example.myappartment.data.PostData
import com.example.myappartment.main.common.ImageComposable
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.ProgressSpinner
import com.example.myappartment.viewModel.AppViewModule


@Composable
fun AddNewPost(
    navController: NavController,
    vm: AppViewModule,
    imageUri: String?,
    post: PostData?
) {
    val image by remember { mutableStateOf(imageUri) }

    var title by rememberSaveable {
        if (post != null)
            mutableStateOf(post.title.toString())
        else
            mutableStateOf("")
    }

    var description by rememberSaveable {
        if (post != null)
            mutableStateOf(post.description.toString())
        else
            mutableStateOf("")
    }

    var location by rememberSaveable {
        if (post != null)
            mutableStateOf(post.location.toString())
        else
            mutableStateOf("")
    }

    var price by rememberSaveable {
        if (post != null)
            mutableStateOf(post.price.toString())
        else
            mutableStateOf("")
    }

    var rooms by rememberSaveable {
        if (post != null)
            mutableStateOf(post.rooms.toString())
        else
            mutableStateOf("")
    }

    var squareFootage by rememberSaveable {
        if (post != null)
            mutableStateOf(post.squareFootage.toString())
        else
            mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Skopje", "Bitola", "Gevgelija", "Kumanovo", "Veles")
    var selected by rememberSaveable {
        if (post != null)
            mutableStateOf(post.city.toString())
        else
            mutableStateOf("")
    }
    var dropdownSize by remember { mutableStateOf(Size.Zero) }


    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current



    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.popBackStack() })
            Text(text = "Save", fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                focusManager.clearFocus()
                if (post != null) {
                    vm.onEditPost(
                        post.postId,
                        post.postImage,
                        title,
                        description,
                        location,
                        price,
                        rooms,
                        squareFootage,
                        selected
                    ) {
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                } else {
                    vm.onNewPost(
                        Uri.parse(imageUri),
                        title,
                        description,
                        location,
                        price,
                        rooms,
                        squareFootage,
                        selected
                    ) {
                        navController.popBackStack()
                    }
                }
            })
        }
        LineDivider()
        if (imageUri == null) {
            ImageComposable(
                data = post?.postImage,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
            )
        } else {
            Image(
                painter = rememberImagePainter(image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Title") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }

        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                readOnly = true,
                value = selected,
                enabled = false,
                onValueChange = { selected = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .onGloballyPositioned { coordinates ->
                        dropdownSize = coordinates.size.toSize()
                    },
                label = { Text("City") },
                trailingIcon = {
                    Icon(icon, null)
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropdownSize.width.toDp() })
            ) {
                cities.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selected = label
                        expanded = false
                    }) {
                        Text(text = label)
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Description") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Location") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Price (in Euros)") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = rooms,
                onValueChange = { rooms = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Rooms") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = squareFootage,
                onValueChange = { squareFootage = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Square Footage") },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
    }
    val inProgress = vm.inProgress.value
    if (inProgress)
        ProgressSpinner()
}

