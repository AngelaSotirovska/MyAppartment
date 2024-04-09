package com.example.myappartment.repositories

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.myappartment.Event
import com.example.myappartment.data.CityData
import com.example.myappartment.repositories.exception.ExceptionHandler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject
import javax.inject.Singleton

const val CITIES = "cities"

@Singleton
class CityRepository @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {
    val popupNotification = mutableStateOf<Event<String>?>(null)
    val cities = mutableStateOf<List<CityData>>(listOf())
    val citiesLoading = mutableStateOf(false)

    init {
        getAllCities()
    }

    fun getAllCities() {
        citiesLoading.value = true
        db.collection(CITIES).get()
            .addOnSuccessListener { documents ->
                convertCities(documents, cities)
                citiesLoading.value = false
            }
            .addOnFailureListener { exc ->
                popupNotification.value = Event(ExceptionHandler.handleException(exc, "Failed to update user profile"))
                citiesLoading.value = false
            }
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
}