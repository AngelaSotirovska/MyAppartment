package com.example.myappartment.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myappartment.data.PostData
import com.example.myappartment.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val popupNotification = postRepository.popupNotification
    val posts = postRepository.posts
    val refreshPostsProgress = postRepository.refreshPostsProgress

    val searchPosts = postRepository.searchPosts
    val searchedPostsProgress = postRepository.searchedPostsProgress

    val allPosts = postRepository.allPosts
    val allPostsProgress = postRepository.allPostsProgress

    val filterPosts = postRepository.filterPosts
    val filterPostsLoading = postRepository.filterPostsLoading

    val post = postRepository.post

    fun getPostById(postId: String) {
        coroutineScope.launch {
            postRepository.getPostById(postId)
        }
    }

    fun deletePost(post: PostData) {
        coroutineScope.launch {
            postRepository.deletePost(post)
        }
    }

    fun searchPosts(searchTerm: String) {
        coroutineScope.launch {
            postRepository.searchPosts(searchTerm)
        }
    }

    fun getPostsByCity(cityId: String?) {
        coroutineScope.launch {
            postRepository.getPostsByCity(cityId)
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
        coroutineScope.launch {
            postRepository.onNewPost(
                imageUri,
                title,
                description,
                location,
                price,
                rooms,
                squareFootage,
                city,
                onPostSuccess
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
        coroutineScope.launch {
            postRepository.onEditPost(
                postId,
                imageUri,
                title,
                description,
                location,
                price,
                rooms,
                squareFootage,
                city,
                onPostSuccess
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.coroutineContext.cancelChildren()
    }
}