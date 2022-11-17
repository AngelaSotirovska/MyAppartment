package com.example.myappartment.authentication

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.Navigation
import androidx.navigation.navDeepLink
import com.example.myappartment.DestinationScreen
import com.example.myappartment.Graph
import com.example.myappartment.ThemeState
import com.example.myappartment.main.common.LineDivider
import com.example.myappartment.main.common.ProgressSpinner
import com.example.myappartment.main.common.UserImageCard
import com.example.myappartment.navigateTo
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun EditProfileScreen(navController: NavController, vm: AppViewModule) {
    val isLoading = vm.inProgress.value
    if (isLoading)
        ProgressSpinner()
    else {
        val userData = vm.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var lastname by rememberSaveable { mutableStateOf(userData?.lastName ?: "") }
        var username by rememberSaveable { mutableStateOf(userData?.username ?: "") }
        var contact by rememberSaveable { mutableStateOf(userData?.contactNumber ?: "") }
        EditProfileContent(
            vm = vm,
            name = name,
            lastname = lastname,
            username = username,
            contact = contact,
            onNameChange = { name = it },
            onLastnameChange = { lastname = it },
            onUsernameChange = { username = it },
            onContactChange = { contact = it },
            onSave = {
                vm.updateProfileData(name, username, lastname, contact)
                navigateTo(navController = navController, DestinationScreen.Profile)
            },
            onBack = { navigateTo(navController = navController, DestinationScreen.Profile) },
            onLogOut = {
                vm.logOut()
                navController.navigate(route = DestinationScreen.Login.route){
                    popUpTo(0)
                }
            }
        )
    }
}

@Composable
fun EditProfileContent(
    vm: AppViewModule,
    name: String,
    lastname: String,
    username: String,
    contact: String,
    onNameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogOut: () -> Unit
) {
    val scrollState = rememberScrollState()
    val imageUrl = vm.userData.value?.imageUrl
    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
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
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBack.invoke() })
                Text(
                    text = "Save",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSave.invoke() })

            }
            LineDivider()

            EditProfileImage(imageUrl = imageUrl, vm = vm)

            LineDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Name", modifier = Modifier.width(200.dp))
                TextField(
                    value = name, onValueChange = onNameChange,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Transparent,
                        textColor = MaterialTheme.colors.onSecondary
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last name", modifier = Modifier.width(200.dp))
                TextField(
                    value = lastname, onValueChange = onLastnameChange,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Transparent,
                        textColor = MaterialTheme.colors.onSecondary
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Username", modifier = Modifier.width(200.dp))
                TextField(
                    value = username, onValueChange = onUsernameChange,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Transparent,
                        textColor = MaterialTheme.colors.onSecondary
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Contact information", modifier = Modifier.width(200.dp))
                TextField(
                    value = contact, onValueChange = onContactChange,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Transparent,
                        textColor = MaterialTheme.colors.onSecondary
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    ThemeState.darkModeState.value = !ThemeState.darkModeState.value
                }) {
                    Text(text = "Theme Toggle Button")
                }
                OutlinedButton(
                    onClick = { onLogOut.invoke() },
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Transparent),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    shape = RoundedCornerShape(10)
                )
                {
                    Text(text = "Log out", color = MaterialTheme.colors.onSecondary)
                }
            }
        }
    }

}

@Composable
fun EditProfileImage(imageUrl: String?, vm: AppViewModule) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
    }
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserImageCard(
                userImage = imageUrl,
                modifier = Modifier
                    .padding(8.dp)
                    .size(80.dp)
            )
            Text(text = "Change profile picture")
        }
        val isLoading = vm.inProgress.value
        if (isLoading)
            ProgressSpinner()
    }
}