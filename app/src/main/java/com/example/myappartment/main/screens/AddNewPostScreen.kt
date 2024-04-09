package com.example.myappartment.main.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.myappartment.R
import com.example.myappartment.data.PostData
import com.example.myappartment.main.common.ImageComposable
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.ProgressSpinner
import com.example.myappartment.main.common.TextField
import com.example.myappartment.viewModel.PostViewModel
import com.example.myappartment.viewModel.UserViewModel


@Composable
fun AddNewPost(
    navController: NavController,
    vm: UserViewModel,
    postVm: PostViewModel,
    imageUri: String?,
    post: PostData?
) {

    val image by remember { mutableStateOf(imageUri) }

    var title by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.title.toString())
        else
            mutableStateOf("")
    }

    var description by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.description.toString())
        else
            mutableStateOf("")
    }

    var location by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.location.toString())
        else
            mutableStateOf("")
    }

    var price by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.price.toString())
        else
            mutableStateOf("")
    }

    var rooms by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.rooms.toString())
        else
            mutableStateOf("")
    }

    var squareFootage by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.squareFootage.toString())
        else
            mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Skopje", "Bitola", "Gevgelija", "Kumanovo", "Veles")
    var selected by rememberSaveable {
        if (post != null)
            mutableStateOf(post!!.city.toString())
        else
            mutableStateOf("")
    }
    var dropdownSize by remember { mutableStateOf(Size.Zero) }


    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()

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
                    postVm.onEditPost(
                        post!!.postId,
                        post!!.postImage,
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
                } else {
                    postVm.onNewPost(
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
                data = post!!.postImage,
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
            TextField(value = title, label = "Title", isForNumbers = false, onValueChange = { newValue ->
                title = newValue
            })
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
                label = { Text(stringResource(R.string.city)) },
                trailingIcon = {
                    Icon(icon, null)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                    backgroundColor = Color.Transparent,
                    disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                    disabledLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
                )

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
            TextField(value = description, label = "Description", isForNumbers = false, onValueChange = { newValue ->
                description = newValue
            })
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(value = location, label = "Location", isForNumbers = false, onValueChange = { newValue ->
                location = newValue
            })
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(value = price, label = "Price", isForNumbers = true, onValueChange = { newValue ->
                price = newValue
            })
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(value = rooms, label = "Rooms", isForNumbers = true, onValueChange = { newValue ->
                rooms = newValue
            })
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(value = squareFootage, label = "Square footage", isForNumbers = true, onValueChange = { newValue ->
                squareFootage = newValue
            })
        }
    }
//    }
    val inProgress = vm.inProgress.value
    if (inProgress)
        ProgressSpinner()
}

