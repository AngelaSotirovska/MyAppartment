package com.example.myappartment.authentication.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myappartment.DestinationScreen
import com.example.myappartment.R
import com.example.myappartment.ThemeState
import com.example.myappartment.main.common.CheckSignIn
import com.example.myappartment.main.common.ProgressSpinner
import com.example.myappartment.navigateTo
import com.example.myappartment.ui.theme.LightPink
import com.example.myappartment.viewModel.UserViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(navController: NavController, vm: UserViewModel) {

    CheckSignIn(vm = vm, navController = navController)

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val focus = LocalFocusManager.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val emailState = remember { mutableStateOf(TextFieldValue()) }
            var isEmailValid by remember { mutableStateOf(true) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }
            var isPasswordEmpty by remember { mutableStateOf(false) }

//            Image(
//                painter = painterResource(id = R.drawable.logo4),
//                contentDescription = null,
//                modifier = Modifier
//                    .width(250.dp)
//                    .height(250.dp)
//                    .padding(top = 16.dp)
//                    .padding(8.dp)
//            )
            Image(
                painter = painterResource(id = if(ThemeState.darkModeState.value) R.drawable.ic_logo_dark else R.drawable.ic_logo_light),
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.logIn), Modifier.padding(8.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif
            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it
                    isEmailValid = validateEmail(it.text) },
                modifier = Modifier
                    .padding(8.dp)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                label = { Text(text = stringResource(R.string.email)) }
            )
//            if (!isEmailValid) {
//                Text(text = "Please enter a valid email", color = Color.Red)
//            }
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                    isPasswordEmpty = it.text.isNotEmpty()},
                modifier = Modifier
                    .padding(8.dp)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                label = { Text(text = stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            Button(
                onClick = {
                    focus.clearFocus(force = true)
                    vm.onLogin(emailState.value.text, passwordState.value.text)
                },
                enabled = (isEmailValid && isPasswordEmpty),
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightPink,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.logIn))
            }
            Text(text = stringResource(R.string.dontHaveAcc),
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Signup)
                    }
            )
        }

        val isLoading = vm.inProgress.value
        if (isLoading) {
            ProgressSpinner()
        }
    }

}

fun validateEmail(email: String):Boolean {
    val emailRegex = Regex("^\\S+@\\S+\\.\\S+$")
    return emailRegex.matches(email)
}