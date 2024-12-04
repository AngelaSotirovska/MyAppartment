package com.example.myappartment.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myappartment.data.Message
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

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    init {
//        userRepository.logOut()

        if (userRepository.currentUser != null) {
            userData.value = userRepository.userData.value
        }
    }

    fun onSignup(username: String, email: String, password: String) {
        coroutineScope.launch {
            userRepository.onSignup(username, email, password)
        }
    }

    fun onLogin(email: String, password: String) {
        coroutineScope.launch {
            userRepository.onLogin(email, password)
        }
    }

    fun updateProfileData(
        name: String, username: String, lastName: String, contactInformation: String
    ) {
        coroutineScope.launch {
            userRepository.createOrUpdateProfile(name, username, lastName, contactInformation)
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        coroutineScope.launch {
            userRepository.uploadImage(uri, onSuccess)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            userRepository.createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun changeMode(darkMode: Boolean) {
        coroutineScope.launch {
            userRepository.changeMode(darkMode)
        }
    }


    fun logOut() {
        coroutineScope.launch {
            userRepository.logOut()
        }
    }

    fun getUserById(userId: String?) {
        coroutineScope.launch {
            userRepository.getUserById(userId)
        }
    }

    fun sendMessage(message: Message) {
        coroutineScope.launch {
            userRepository.sendMessage(message);
        }
    }

    fun getConversationMessages(otherUserId: String) {
        coroutineScope.launch {
            userRepository.getConversationMessages(otherUserId);
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.coroutineContext.cancelChildren()
    }
}