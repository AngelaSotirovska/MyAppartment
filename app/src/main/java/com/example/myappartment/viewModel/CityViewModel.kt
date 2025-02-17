package com.example.myappartment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val popupNotification = cityRepository.popupNotification
    val cities = cityRepository.cities
    val citiesLoading = cityRepository.citiesLoading

    fun getAllCities() {
        viewModelScope.launch(Dispatchers.IO) {
            cityRepository.getAllCities()
        }
    }
}