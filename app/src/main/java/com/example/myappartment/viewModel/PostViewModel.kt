package com.example.myappartment.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappartment.data.PostData
import com.example.myappartment.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
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
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.getPostById(postId)
        }
    }

    fun deletePost(post: PostData) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.deletePost(post)
        }
    }

    fun searchPosts(searchTerm: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.searchPosts(searchTerm)
        }
    }

    fun getPostsByCity(cityId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
}