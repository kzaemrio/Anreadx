package com.kz.anreadx.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kz.anreadx.R

@Composable
fun FeedDetail(
    onBackClick: () -> Unit,
    viewModel: FeedDetailViewModel =  viewModel()
) {
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
        val list by viewModel
            .detailItemList
            .collectAsState(
                initial = emptyList()
            )
        FeedDetailList(list)
    }
}

@Composable
fun FeedDetailList(list: List<DetailItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(list) {
            when (it) {
                is DetailItem.Image -> {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = it.url,
                        contentDescription = it.url
                    )
                }
                is DetailItem.Text -> {
                    Text(text = it.value)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
