package com.example.myappartment

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object ThemeState {
    var darkModeState : MutableState<Boolean> = mutableStateOf(false)
}