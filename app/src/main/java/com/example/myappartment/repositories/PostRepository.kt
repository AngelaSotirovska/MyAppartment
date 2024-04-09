package com.example.myappartment.repositories


import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myappartment.Event
import com.example.myappartment.MainActivity
import com.example.myappartment.ThemeState
import com.example.myappartment.data.PostData
import com.example.myappartment.data.UserData
import com.example.myappartment.repositories.exception.ExceptionHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


const val POSTS = "posts"

@Singleton
class PostRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : ViewModel() {

    val popupNotification = mutableStateOf<Event<String>?>(null)
    val posts = mutableStateOf<List<PostData>>(listOf())
    val refreshPostsProgress = mutableStateOf(false)

    val searchPosts = mutableStateOf<List<PostData>>(listOf())
    val searchedPostsProgress = mutableStateOf(false)

    val allPosts = mutableStateOf<List<PostData>>(listOf())
    val allPostsProgress = mutableStateOf(false)

    val filterPosts = mutableStateOf<List<PostData>>(listOf())
    val filterPostsLoading = mutableStateOf(false)
    private val filerWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "in", "it")

    private val currentUser = mutableStateOf<UserData?>(null)
    val post = mutableStateOf<PostData?>(null);

    init {
        getAllPosts()
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                val user = auth.currentUser

                user?.uid?.let { uid ->
                    db.collection(USERS).document(uid).get().addOnSuccessListener {
                        val user = it.toObject<UserData>()
                        currentUser.value = user
                        refreshPosts()
                    }.addOnFailureListener { exc ->
                        popupNotification.value = Event(
                            ExceptionHandler.handleException(
                                exc, "Cannot retrieve user data"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getAllPosts() {
        allPostsProgress.value = true
        db.collection(POSTS).get().addOnSuccessListener { documents ->
                convertPosts(documents, allPosts)
                allPostsProgress.value = false
                refreshPosts()
            }.addOnFailureListener { exc ->
                popupNotification.value =
                    Event(ExceptionHandler.handleException(customMessage = "Unable to fetch posts"))
                allPostsProgress.value = false
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

    private fun refreshPosts() {
        val currentUuid = currentUser.value?.userId
        if (currentUuid != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).whereEqualTo("userId", currentUuid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                }.addOnFailureListener { exc ->
                    popupNotification.value =
                        Event(ExceptionHandler.handleException(customMessage = "Unable to fetch posts"))
                    refreshPostsProgress.value = false
                }
        } else {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Error: username unavailable. Unable to refresh posts"))
        }

    }

    fun getPostById(postId: String) {
        refreshPostsProgress.value = true
        db.collection(POSTS).document(postId).get().addOnSuccessListener {
                refreshPostsProgress.value = false
                post.value = it.toObject()
            }.addOnFailureListener { exc ->
                popupNotification.value = Event(
                    ExceptionHandler.handleException(
                        exc, customMessage = "Unable to get post. Not correct"
                    )
                )
                refreshPostsProgress.value = false
            }
    }

    fun deletePost(post: PostData) {
        val currentUuid = auth.currentUser?.uid
        if (post.postId != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).document(post.postId).delete().addOnSuccessListener {
                    db.collection(POSTS).whereEqualTo("userId", currentUuid).get()
                        .addOnSuccessListener { documents ->
                            convertPosts(documents, posts)
                            refreshPostsProgress.value = false
                            refreshPosts()
                            getAllPosts()
                        }.addOnFailureListener {
                            popupNotification.value =
                                Event(ExceptionHandler.handleException(customMessage = "Unable to fetch posts"))
                            refreshPostsProgress.value = false
                        }
                }.addOnFailureListener { exc ->
                    popupNotification.value = Event(
                        ExceptionHandler.handleException(
                            exc, customMessage = "Unable to delete post. Not correct"
                        )
                    )
                    refreshPostsProgress.value = false
                }
        } else {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Unable to delete post"))
        }
    }

    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            searchedPostsProgress.value = true
            searchPosts.value = allPosts.value.filter { postData ->
                postData.searchTerms?.any { term -> term.contains(searchTerm) } == true
            }
        }
        searchedPostsProgress.value = false
    }

    fun getPostsByCity(cityId: String?) {
        filterPostsLoading.value = true
        db.collection(POSTS).whereEqualTo("city", cityId).get().addOnSuccessListener { documents ->
                convertPosts(documents, filterPosts)
                filterPostsLoading.value = false
            }.addOnFailureListener { exc ->
                popupNotification.value = Event(
                    ExceptionHandler.handleException(
                        exc, customMessage = "Unable to fetch posts"
                    )
                )
                filterPostsLoading.value = false
            }
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
        val currentUid = auth.currentUser?.uid
        val userData = mutableStateOf<UserData?>(null)
        if (currentUid != null) {
            db.collection(USERS).document(currentUid).get().addOnSuccessListener {
                    val user = it.toObject<UserData>()
                    userData.value = user
//                    inProgress.value = false
                    ThemeState.darkModeState.value = userData.value?.darkMode!!
                }.addOnFailureListener { exc ->
//                    inProgress.value = false
                    popupNotification.value =
                        Event(ExceptionHandler.handleException(exc, "Cannot retrieve user data"))
                }
        }
        val currentUsername = userData.value?.username
        val currentImage = userData.value?.imageUrl
        val currentContactNumber = userData.value?.contactNumber

        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()


            val searchTerms = title.split(" ", ".", ",", "?", "!", "#", ":").map { it.lowercase() }
                .filter { it.isNotEmpty() and !filerWords.contains(it) } + location.split(
                    " ",
                    ".",
                    ",",
                    "?",
                    "!",
                    "#",
                    ":"
                ).map { it.lowercase() }.filter { it.isNotEmpty() and !filerWords.contains(it) }

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
            db.collection(POSTS).document(postUuid).set(post).addOnSuccessListener {
                    popupNotification.value = Event("Post successfully created")
//                    userViewModel.inProgress.value = false
                    refreshPosts()
                    getAllPosts()
                    onPostSuccess.invoke()
                }.addOnFailureListener { exc ->
                    popupNotification.value =
                        Event(ExceptionHandler.handleException(exc, "Unable to create post"))
//                    userViewModel.inProgress.value = false
                }
        } else {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Error: username unavailable. Unable to create post"))
//            userViewModel.logOut()
//            userViewModel.inProgress.value = false
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
//        userViewModel.inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val userData = mutableStateOf<UserData?>(null)
        if (currentUid != null) {
            db.collection(USERS).document(currentUid).get().addOnSuccessListener {
                    val user = it.toObject<UserData>()
                    userData.value = user
//                    inProgress.value = false
                    ThemeState.darkModeState.value = userData.value?.darkMode!!
                    refreshPosts()
                    //                postVm.getAllPosts()
                }.addOnFailureListener { exc ->
//                    inProgress.value = false
                    popupNotification.value =
                        Event(ExceptionHandler.handleException(exc, "Cannot retrieve user data"))
                }
        }
        val currentUsername = userData.value?.username
        val currentImage = userData.value?.imageUrl
        val currentContactNumber = userData.value?.contactNumber

        if (currentUid != null) {
            if (postId != null) {
                db.collection(POSTS).document(postId).get().addOnSuccessListener {
                        val filler = MainActivity.filerWords
                        val searchTerms =
                            title.split(" ", ".", ",", "?", "!", "#", ":").map { it.lowercase() }
                                .filter { it.isNotEmpty() and !filler.contains(it) } + location.split(
                                    " ",
                                    ".",
                                    ",",
                                    "?",
                                    "!",
                                    "#",
                                    ":"
                                ).map { it.lowercase() }
                                .filter { it.isNotEmpty() and !filler.contains(it) }
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
                        it.reference.update(editPost.toMap()).addOnSuccessListener {
                                refreshPosts()
                                getAllPosts()
                                onPostSuccess.invoke()
//                                userViewModel.inProgress.value = false
                            }.addOnFailureListener {
                                popupNotification.value = Event(
                                    ExceptionHandler.handleException(
                                        it, "Cannot update post"
                                    )
                                )
//                                userViewModel.inProgress.value = false
                            }
                    }
            }
        } else {
            popupNotification.value =
                Event(ExceptionHandler.handleException(customMessage = "Error: post unavailable. Unable to create post"))
//            userViewModel.inProgress.value = false
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

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
//        inProgress.value = true;

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
//                inProgress.value = false
            }
    }
}