package com.example.myappartment.main.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myappartment.R
import com.example.myappartment.data.CityData
import com.example.myappartment.viewModel.UserViewModel

@Composable
fun CitiesList(
    vm: UserViewModel,
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
                Text(text = stringResource(R.string.noCities))
            }
        }
    } else {
        LazyRow(
//            cells = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            .clickable { onCityClick(item) },
        shape = MaterialTheme.shapes.medium,
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Place, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
            Text(text = "${item.name}", modifier = Modifier.padding(end = 8.dp))
        }
    }
}
