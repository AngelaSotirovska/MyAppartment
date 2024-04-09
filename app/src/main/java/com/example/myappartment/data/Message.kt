package com.example.myappartment.data

data class Message(
    var message: String? = "",
    var senderId: String? = "",
    var receiverId: String? = "",
    var sendDate: Long? = null,
) {
    fun toMap() = mapOf(
        "message" to message,
        "senderId" to senderId,
        "receiverId" to receiverId,
        "sendDate" to sendDate,
    )
}
