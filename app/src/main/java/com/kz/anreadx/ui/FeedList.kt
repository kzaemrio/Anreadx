package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.combine
import com.kz.anreadx.ktx.ifTrue
import com.kz.anreadx.ktx.then
import kotlinx.coroutines.launch

@Composable
fun FeedList(navToDetail: (String) -> Unit) {
    val viewModel = vmKodein(::FeedListViewModel)
    val sendChannel = viewModel.sendChannel
    val state: UiState by viewModel.uiStateFlow.collectAsState()

    FeedList(
        state = state,
        onRefresh = {
            viewModel.viewModelScope.launch {
                sendChannel.send(OnRefresh)
            }
        },
        onClear = {
            viewModel.viewModelScope.launch {
                sendChannel.send(OnReadAll)
            }
        },
        onItemClick = combine({
            viewModel.viewModelScope.launch {
                sendChannel.send(OnFeedItemClick(it))
            }
        }, FeedItem::id.then(navToDetail))
    )
}

@Composable
fun FeedList(
    state: UiState,
    onRefresh: () -> Unit,
    onClear: () -> Unit,
    onItemClick: (FeedItem) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    val errorMessage = state.errorMessage
    errorMessage.isNotBlank().ifTrue {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
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
fun FeedList(list: List<FeedItem>, onItemClick: (FeedItem) -> Unit) {
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
fun Item(item: FeedItem, onItemClick: (FeedItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
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

sealed interface UiEvent
object OnRefresh : UiEvent
object OnReadAll : UiEvent
data class OnFeedItemClick(val feedItem: FeedItem) : UiEvent
