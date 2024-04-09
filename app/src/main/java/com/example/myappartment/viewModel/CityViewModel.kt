package com.example.myappartment.viewModel

import androidx.lifecycle.ViewModel
import com.example.myappartment.repositories.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val cityRepository: CityRepository
) : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val popupNotification = cityRepository.popupNotification
    val cities = cityRepository.cities
    val citiesLoading = cityRepository.citiesLoading

    fun getAllCities() {
        coroutineScope.launch {
            cityRepository.getAllCities()
        }
    }
}