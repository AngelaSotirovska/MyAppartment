package com.example.myappartment.repositories.exception

object ExceptionHandler {
    fun handleException(exception: Exception? = null, customMessage: String = ""): String {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        return if (customMessage.isEmpty()) errorMessage else "$customMessage: $errorMessage"
    }
}