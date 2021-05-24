package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.ifFalse

@Composable
fun FeedList(store: FeedListStore = FeedListStore(), navToDetail: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = store::onClearAllClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_clear_all_24),
                            contentDescription = stringResource(id = R.string.menu_clear_all)
                        )
                    }
                }
            )
        }
    ) {
        val state = rememberSwipeRefreshState(store.isLoading)
        SwipeRefresh(state = state, onRefresh = {
            state.isRefreshing.ifFalse { store.onRefresh() }
        }) {
            val onItemClick: (String) -> Unit = navToDetail
            FeedList(
                list = store.list,
                onItemClick = onItemClick
            )
            Button(onClick = {
                navToDetail("2333")
            }) {

            }
        }
    }
}

@Composable
fun FeedList(list: List<ViewItem>, onItemClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        items(items = list, key = { it.id }) {
            Item(item = it, onItemClick)
        }
    }
}

@Composable
fun Item(item: ViewItem, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Column(Modifier.weight(1F)) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (item.done) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.timeLabel,
                style = MaterialTheme.typography.body2,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.content,
                style = MaterialTheme.typography.body2,
                color = if (item.done) Color.LightGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.width(14.dp))
    }
}
