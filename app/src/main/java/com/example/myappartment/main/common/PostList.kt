package com.example.myappartment.main.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappartment.data.PostData
import com.example.myappartment.ui.theme.Gray50
import com.example.myappartment.viewModel.AppViewModule

@Composable
fun PostList(
    vm: AppViewModule,
    isContextLoading: Boolean,
    postsLoading: Boolean,
    posts: List<PostData>,
    modifier: Modifier,
    noPostsMessage: String,
    onPostClick: (PostData) -> Unit
) {
    if (postsLoading) {
        ProgressSpinner()
    } else if (posts.isEmpty()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isContextLoading) {
                Text(text = noPostsMessage)
            }
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(items = posts) { post ->
                PostItem(item = post, vm = vm, onPostClick = onPostClick)
            }
        }
    }
}

@Composable
fun PostItem(item: PostData, vm: AppViewModule, onPostClick: (PostData) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp)
            .clickable { item?.let { post -> onPostClick(post) } },
        shape = MaterialTheme.shapes.medium,
        elevation = 5.dp,
        backgroundColor = Gray50
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageComposable(
                data = item.postImage, modifier = Modifier
                    .size(140.dp)
                    .padding(8.dp), contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = item.title ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 2
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = if (item.price == null) "" else "${item.price} euros")
                }
            }
        }
    }
}