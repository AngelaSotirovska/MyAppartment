package com.example.myappartment.viewModel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myappartment.data.CityData
import com.example.myappartment.Event
import com.example.myappartment.data.PostData
import com.example.myappartment.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.*
import javax.inject.Inject

const val USERS = "users"
const val POSTS = "posts"
const val CITIES = "cities"

@HiltViewModel
class AppViewModule @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val getUserDataById = mutableStateOf<UserData?>(null)
    val getUserDataByIdLoading = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    val posts = mutableStateOf<List<PostData>>(listOf())
    val refreshPostsProgress = mutableStateOf(false)

    val searchPosts = mutableStateOf<List<PostData>>(listOf())
    val searchedPostsProgress = mutableStateOf(false)

    val allPosts = mutableStateOf<List<PostData>>(listOf())
    val allPostsProgress = mutableStateOf(false)

    val cities = mutableStateOf<List<CityData>>(listOf())
    val citiesLoading = mutableStateOf(false)

    val filterPosts = mutableStateOf<List<PostData>>(listOf())
    val filterPostsLoading = mutableStateOf(false)
    val filerWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "in", "it")

    val isDarkMode = mutableStateOf(false)

    init {
//        auth.signOut()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
        getAllCities()
    }

    fun onSignup(username: String, email: String, password: String) {
        inProgress.value = true

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all the data")
            return
        }

        db.collection(USERS).whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(customMessage = "Username already exists")
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                createOrUpdateProfile(username = username)
                            } else {
                                handleException(task.exception, "Sign up failed")

                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener {}
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all the data")
            return
        }

        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        getUserData(uid)
                    }
                } else {
                    handleException(task.exception, "Login failed")
                    inProgress.value = false
                }
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Login failed")
                inProgress.value = false
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        lastName: String? = null,
        contactNumber: String? = null,
        imageUrl: String? = null,
    ) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            lastName = lastName ?: userData.value?.lastName,
            username = username ?: userData.value?.username,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            contactNumber = contactNumber ?: userData.value?.contactNumber
        )
        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                inProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update user")
                                inProgress.value = false
                            }
                    } else {
                        db.collection(USERS).document(uid).set(userData)
                        getUserData(uid)
                        inProgress.value = false
                    }
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot create user")
                    inProgress.value = false
                }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USERS).document(uid).get()
            .addOnSuccessListener {
                val user = it.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                refreshPosts()
                getAllPosts()
            }
            .addOnFailureListener { exc ->
                inProgress.value = false
                handleException(exc, "Cannot retrieve user data")
            }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage: $errorMessage"
        popupNotification.value = Event(message)
    }

    fun updateProfileData(
        name: String,
        username: String,
        lastName: String,
        contactInformation: String
    ) {
        createOrUpdateProfile(name, username, lastName, contactInformation);
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true;

        val storageReference = storage.reference
        val uuid = UUID.randomUUID()
        val imageReference = storageReference.child("images/$uuid")
        val uploadTask = imageReference.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }
            .addOnFailureListener { exc ->
                handleException(exc)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }


    fun logOut() {
        auth.signOut();
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged out")
        searchPosts.value = listOf()
    }

    private fun onCreatePost(
        imageUri: Uri,
        title: String,
        description: String,
        location: String,
        price: Int,
        rooms: Int,
        squareFootage: String,
        city: String,
        onPostSuccess: () -> Unit,
    ) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentImage = userData.value?.imageUrl
        val currentContactNumber = userData.value?.contactNumber

        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()


            val searchTerms = title
                .split(" ", ".", ",", "?", "!", "#", ":")
                .map { it.lowercase() }
                .filter { it.isNotEmpty() and !filerWords.contains(it) } +
                    location
                        .split(" ", ".", ",", "?", "!", "#", ":")
                        .map { it.lowercase() }
                        .filter { it.isNotEmpty() and !filerWords.contains(it) }

            val post = PostData(
                postUuid,
                currentUid,
                currentUsername,
                currentImage,
                currentContactNumber,
                imageUri.toString(),
                title,
                description,
                location,
                price,
                rooms,
                squareFootage,
                city,
                System.currentTimeMillis(),
                searchTerms
            )
            db.collection(POSTS).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = Event("Post successfully created")
                    inProgress.value = false
                    refreshPosts()
                    getAllPosts()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Unable to create post")
                    inProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to create post")
            logOut()
            inProgress.value = false
        }
    }

    fun onNewPost(
        imageUri: Uri,
        title: String,
        description: String,
        location: String,
        price: String,
        rooms: String,
        squareFootage: String,
        city: String,
        onPostSuccess: () -> Unit
    ) {
        uploadImage(imageUri) {
            onCreatePost(
                imageUri = it,
                title = title,
                description = description,
                location = location,
                price = price.toInt(),
                rooms = rooms.toInt(),
                squareFootage = squareFootage,
                city = city,
                onPostSuccess = onPostSuccess
            )
        }
    }

    fun onEditPost(
        postId: String?,
        imageUri: String?,
        title: String,
        description: String,
        location: String,
        price: String,
        rooms: String,
        squareFootage: String,
        city: String,
        onPostSuccess: () -> Unit
    ) {
        onEditPostData(
            postId = postId,
            imageUri = imageUri,
            title = title,
            description = description,
            location = location,
            price = price.toInt(),
            rooms = rooms.toInt(),
            squareFootage = squareFootage,
            city = city,
            onPostSuccess = onPostSuccess
        )
    }

    private fun refreshPosts() {
        val currentUuid = auth.currentUser?.uid
        if (currentUuid != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).whereEqualTo("userId", currentUuid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(customMessage = "Unable to fetch posts")
                    refreshPostsProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to refresh posts")
            logOut()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, postsState: MutableState<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach { doc ->
            val post = doc.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending { it.time }
        postsState.value = sortedPosts
    }

    private fun convertCities(documents: QuerySnapshot, citiesState: MutableState<List<CityData>>) {
        val newCities = mutableListOf<CityData>()
        documents.forEach { doc ->
            val city = doc.toObject<CityData>()
            newCities.add(city)
        }
        val sortedCities = newCities.sortedBy { it.name }
        citiesState.value = sortedCities
    }

    fun deletePost(post: PostData) {
        val currentUuid = auth.currentUser?.uid
        if (post.postId != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).document(post.postId).delete()
                .addOnSuccessListener {
                    db.collection(POSTS).whereEqualTo("userId", currentUuid).get()
                        .addOnSuccessListener { documents ->
                            convertPosts(documents, posts)
                            refreshPostsProgress.value = false
                            refreshPosts()
                            getAllPosts()
                        }
                        .addOnFailureListener {
                            handleException(customMessage = "Unable to fetch posts")
                            refreshPostsProgress.value = false
                        }
                }
                .addOnFailureListener { exc ->
                    handleException(customMessage = "Unable to delete post. Not correct")
                    refreshPostsProgress.value = false
                }
        } else {
            handleException(customMessage = "Unable to delete post")
        }
    }


    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            searchedPostsProgress.value = true
            db.collection(POSTS).whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get()
                .addOnSuccessListener {
                    refreshPosts()
                    convertPosts(it, searchPosts)
                    searchedPostsProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot search posts")
                    searchedPostsProgress.value = false
                }
        }
    }

    private fun getAllPosts() {
        allPostsProgress.value = true
        db.collection(POSTS).get()
            .addOnSuccessListener { documents ->
                convertPosts(documents, allPosts)
                allPostsProgress.value = false
                refreshPosts()
            }
            .addOnFailureListener { exc ->
                handleException(customMessage = "Unable to fetch posts")
                allPostsProgress.value = false
            }
    }

    private fun getAllCities() {
        citiesLoading.value = true
        db.collection(CITIES).get()
            .addOnSuccessListener { documents ->
                convertCities(documents, cities)
                allPostsProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(customMessage = "Unable to fetch cities")
                allPostsProgress.value = false
            }
    }

    fun getPostsByCity(cityId: String?) {
        filterPostsLoading.value = true
        db.collection(POSTS).whereEqualTo("city", cityId).get()
            .addOnSuccessListener { documents ->
                convertPosts(documents, filterPosts)
                filterPostsLoading.value = false
            }
            .addOnFailureListener { exc ->
                handleException(customMessage = "Unable to fetch posts")
                filterPostsLoading.value = false
            }
    }

    fun getUserById(userId: String?) {
        getUserDataByIdLoading.value = true
        if (userId != null) {
            db.collection(USERS).document(userId).get()
                .addOnSuccessListener {
                    getUserDataById.value = it.toObject<UserData>()
                    getUserDataByIdLoading.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot retrieve user data")
                    getUserDataByIdLoading.value = false
                }
        }
    }

    private fun onEditPostData(
        postId: String?,
        imageUri: String?,
        title: String,
        description: String,
        location: String,
        price: Int,
        rooms: Int,
        squareFootage: String,
        city: String,
        onPostSuccess: () -> Unit,
    ) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentImage = userData.value?.imageUrl
        val currentContactNumber = userData.value?.contactNumber

        if (currentUid != null) {
            if (postId != null) {
                db.collection(POSTS).document(postId).get()
                    .addOnSuccessListener {
                        val searchTerms = title
                            .split(" ", ".", ",", "?", "!", "#", ":")
                            .map { it.lowercase() }
                            .filter { it.isNotEmpty() and !filerWords.contains(it) } +
                                location
                                    .split(" ", ".", ",", "?", "!", "#", ":")
                                    .map { it.lowercase() }
                                    .filter { it.isNotEmpty() and !filerWords.contains(it) }
                        val editPost = PostData(
                            postId,
                            currentUid,
                            currentUsername,
                            currentImage,
                            currentContactNumber,
                            imageUri.toString(),
                            title,
                            description,
                            location,
                            price,
                            rooms,
                            squareFootage,
                            city,
                            System.currentTimeMillis(),
                            searchTerms
                        )
                        it.reference.update(editPost.toMap())
                            .addOnSuccessListener {
                                refreshPosts()
                                getAllPosts()
                                onPostSuccess.invoke()
                                inProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update post")
                                inProgress.value = false
                            }
                    }
            }
        } else {
            handleException(customMessage = "Error: post unavailable. Unable to create post")
            inProgress.value = false
        }
    }
}