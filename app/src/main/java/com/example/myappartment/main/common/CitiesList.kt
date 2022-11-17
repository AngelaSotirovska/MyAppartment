package com.example.myappartment.main.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myappartment.data.CityData
import com.example.myappartment.ui.theme.Gray50
import com.example.myappartment.viewModel.AppViewModule

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CitiesList(
    vm: AppViewModule,
    isContextLoading: Boolean,
    citiesLoading: Boolean,
    cities: List<CityData>,
    modifier: Modifier,
    onCityClick: (CityData) -> Unit
) {
    if (citiesLoading) {
        ProgressSpinner()
    } else if (cities.isEmpty()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isContextLoading) {
                Text(text = "No cities to show")
            }
        }
    } else {
        LazyVerticalGrid(
            cells = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cities) { item ->
                CityItem(item = item, onCityClick = onCityClick)
            }
        }
    }
}

@Composable
fun CityItem(item: CityData, onCityClick: (CityData) -> Unit) {

    Card(
        modifier = Modifier
            .height(50.dp)
            .padding(4.dp)
            .clickable { item?.let { city -> onCityClick(city) } },
        shape = MaterialTheme.shapes.medium,
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Place, contentDescription = null)
            Text(text = "${item.name}")
        }
    }
}
