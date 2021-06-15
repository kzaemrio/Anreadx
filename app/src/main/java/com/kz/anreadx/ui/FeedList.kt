package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R

@Composable
fun FeedList(navToDetail: (String) -> Unit) {

    val viewModel = vmKodein(::FeedListViewModel)
    val state: UiState by viewModel.uiStateFlow.collectAsState()

    FeedList(
        state = state,
        onRefresh = viewModel::updateList,
        onClear = viewModel::clearAll,
        onItemClick = navToDetail
    )
}

@Composable
fun FeedList(
    state: UiState,
    onRefresh: () -> Unit,
    onClear: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onClear) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_clear_all_24),
                            contentDescription = stringResource(id = R.string.menu_clear_all)
                        )
                    }
                }
            )
        }
    ) {
        SwipeRefresh(state = rememberSwipeRefreshState(state.isRefreshing), onRefresh = onRefresh) {
            FeedList(
                list = state.list,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun FeedList(list: List<FeedItem>, onItemClick: (String) -> Unit) {
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
fun Item(item: FeedItem, onItemClick: (String) -> Unit) {
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
