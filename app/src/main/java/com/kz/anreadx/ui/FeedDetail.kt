package com.kz.anreadx.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.kz.anreadx.R

@Composable
fun FeedDetail(onBackClick: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = stringResource(id = R.string.menu_clear_all)
                    )
                }
            }
        )
    }) {
        val list by vmKodein(::FeedDetailViewModel)
            .detailItemList
            .collectAsState(
                context = rememberCoroutineScope().coroutineContext,
                initial = emptyList()
            )
        FeedDetailList(list)
    }
}

@Composable
fun FeedDetailList(list: List<DetailItem>) {
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(list) {
            when (it) {
                is DetailItem.Image -> Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    painter = rememberCoilPainter(it.url),
                    contentDescription = it.url,
                )
                is DetailItem.Text -> Text(text = it.value)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
