package com.example.myappartment.repositories

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.myappartment.Event
import com.example.myappartment.ThemeState
import com.example.myappartment.data.Message
import com.example.myappartment.data.PostData
import com.example.myappartment.data.UserData
import com.example.myappartment.repositories.exception.ExceptionHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

const val USERS = "users"

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val getUserDataById = mutableStateOf<UserData?>(null)
    val getUserDataByIdLoading = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)
    val conversationMessages = mutableStateOf<List<Message>>(listOf())

    init {
        auth.signOut()
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignup(username: String, email: String, password: String) {
        inProgress.value = true

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Please fill in all the data"))
            return
        }

        db.collection(USERS).whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    popupNotification.value =
                        Event(ExceptionHandler.handleException(customMessage = "Username already exists"))
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                createOrUpdateProfile(username = username, darkMode = false)
                                signedIn.value = true
                            } else {
                                popupNotification.value = Event(
                                    ExceptionHandler.handleException(
                                        task.exception, "Sign up failed"
                                    )
                                )

                            }
                            inProgress.value = false
                        }
                }
            }.addOnFailureListener {}
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Please fill in all the data"))
            return
        }

        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signedIn.value = true
                inProgress.value = false
                auth.currentUser?.uid?.let { uid ->
                    getUserData(uid)
                }
            } else {
                popupNotification.value = Event(
                    ExceptionHandler.handleException(task.exception, "Login failed")
                )
                inProgress.value = false
            }
        }.addOnFailureListener { exc ->
            popupNotification.value = Event(ExceptionHandler.handleException(exc, "Login failed"))
            inProgress.value = false
        }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        lastName: String? = null,
        contactNumber: String? = null,
        darkMode: Boolean? = null,
        imageUrl: String? = null,
    ) {
        val uid = currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            lastName = lastName ?: userData.value?.lastName,
            username = username ?: userData.value?.username,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            contactNumber = contactNumber ?: userData.value?.contactNumber,
            darkMode = darkMode ?: userData.value?.darkMode
        )
        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap()).addOnSuccessListener {
                        this.userData.value = userData
                        inProgress.value = false
                    }.addOnFailureListener {
                        popupNotification.value =
                            Event(ExceptionHandler.handleException(it, "Cannot update user"))
                        inProgress.value = false
                    }
                } else {
                    db.collection(USERS).document(uid).set(userData).addOnSuccessListener {
                        getUserData(uid)
                        inProgress.value = false

                    }.addOnFailureListener { exc ->
                        popupNotification.value =
                            Event(ExceptionHandler.handleException(exc, "Unable to create user"))
                    }

                }
            }.addOnFailureListener { exc ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(exc, "Cannot create user"))
                inProgress.value = false
            }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USERS).document(uid).get().addOnSuccessListener {
            val user = it.toObject<UserData>()
            userData.value = user
            inProgress.value = false
            ThemeState.darkModeState.value = userData.value?.darkMode!!
        }.addOnFailureListener { exc ->
            inProgress.value = false
            popupNotification.value =
                Event(ExceptionHandler.handleException(exc, "Cannot retrieve user data"))
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true;

        val storageReference = storage.reference
        val uuid = UUID.randomUUID()
        val imageReference = storageReference.child("images/$uuid")
        val uploadTask = imageReference.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }.addOnFailureListener { exc ->
            popupNotification.value =
                Event(ExceptionHandler.handleException(exc, "Unable to upload image"))
            inProgress.value = false
        }
    }

    fun changeMode(darkMode: Boolean) {
        inProgress.value = true
        val uid = auth.currentUser?.uid


        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS).document(uid).get().addOnSuccessListener {
                ThemeState.darkModeState.value = darkMode
                it.reference.update("darkMode", darkMode)
                inProgress.value = false
            }.addOnFailureListener { exc ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(exc, "Cannot change screen mode."))
                inProgress.value = false
            }
        }
    }


    fun logOut() {
        auth.signOut();
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged out")
    }

    fun getUserById(userId: String?) {
        getUserDataByIdLoading.value = true
        if (userId != null) {
            db.collection(USERS).document(userId).get().addOnSuccessListener {
                getUserDataById.value = it.toObject<UserData>()
                getUserDataByIdLoading.value = false
            }.addOnFailureListener { exc ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(exc, "Cannot retrieve user data"))
                getUserDataByIdLoading.value = false
            }
        }
    }

    fun sendMessage(message: Message) {
        db.collection("messages")
            .add(message.toMap())
            .addOnFailureListener { e ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(e, "Cannot send message"))
            }
    }

    fun getConversationMessages(otherUserId: String) {
        db.collection("messages")
            .whereEqualTo("senderId", currentUser?.uid)
            .whereEqualTo("receiverId", otherUserId).get().addOnSuccessListener { documents ->
                convertMessages(documents, conversationMessages)
            }.addOnFailureListener { exc ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(customMessage = "Unable to fetch messages"))
            }

    }

    private fun convertMessages(documents: QuerySnapshot, messagesState: MutableState<List<Message>>) {
        val newMessages = mutableListOf<Message>()
        documents.forEach { doc ->
            val message = doc.toObject<Message>()
            newMessages.add(message)
        }
        val sortedMessages = newMessages.sortedBy { it.sendDate }
        messagesState.value = sortedMessages
    }

    fun listenForMessages(receiverId: String, onMessageReceived: (Message) -> Unit) {
        db.collection("messages")
            .whereEqualTo("receiverId", receiverId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Error occurred while listening for messages
                    return@addSnapshotListener
                }

                value?.documents?.forEach { document ->
                    val receivedMessage = document.toObject<Message>()
                    if (receivedMessage != null) {
                        onMessageReceived(receivedMessage)
                    }
                }
            }
    }
}