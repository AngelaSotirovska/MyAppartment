package com.example.myappartment.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappartment.data.Message
import com.example.myappartment.data.UserData
import com.example.myappartment.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val signedIn = userRepository.signedIn
    val inProgress = userRepository.inProgress
    val userData = userRepository.userData
    val getUserDataById = userRepository.getUserDataById
    val getUserDataByIdLoading = userRepository.getUserDataByIdLoading
    val popupNotification = userRepository.popupNotification
    val conversationMessages = userRepository.conversationMessages


    init {
//        userRepository.logOut()

        if (userRepository.currentUser != null) {
            userData.value = userRepository.userData.value
        }
    }

    fun onSignup(username: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.onSignup(username, email, password)
        }
    }

    fun onLogin(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.onLogin(email, password)
        }
    }

    fun updateProfileData(
        name: String, username: String, lastName: String, contactInformation: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createOrUpdateProfile(name, username, lastName, contactInformation)
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.uploadImage(uri, onSuccess)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            userRepository.createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun changeMode(darkMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.changeMode(darkMode)
        }
    }


    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.logOut()
        }
    }

    fun getUserById(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserById(userId)
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.sendMessage(message);
        }
    }

    fun getConversationMessages(otherUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getConversationMessages(otherUserId);
        }
    }

    fun getUser(id: String, callback: (UserData?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser(id, callback)
        }
    }
}