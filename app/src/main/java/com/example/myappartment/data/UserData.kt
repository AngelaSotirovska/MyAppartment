package com.example.myappartment.data

data class UserData(
    var userId: String? = null,
    var name: String? = null,
    var lastName: String? = null,
    var username: String? = null,
    var imageUrl: String? = null,
    var contactNumber: String? = null,
    var likedPosts: List<String>? = null
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "lastName" to lastName,
        "username" to username,
        "imageUrl" to imageUrl,
        "contactNumber" to contactNumber,
        "likedPosts" to likedPosts
    )
}