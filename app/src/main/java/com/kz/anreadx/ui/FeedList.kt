package com.kz.anreadx.ui

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.combine
import com.kz.anreadx.ktx.then

@Composable
fun FeedList(
    navToDetail: (String) -> Unit,
    viewModel: FeedListViewModel = viewModel()
) {
    FeedList(
        state = viewModel.uiState,
        uiEvent = viewModel.uiEvent,
        onRefresh = viewModel::onRefresh,
        onItemClick = combine(
            viewModel::onFeedItemClick,
            FeedItem::id.then(navToDetail)
        ),
        onListSettle = viewModel::onListSettle
    )
}

@Composable
fun FeedList(
    state: UiState,
    uiEvent: UiEvent,
    onRefresh: () -> Unit,
    onItemClick: (FeedItem) -> Unit,
    onListSettle: (String, Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(key1 = uiEvent) {
        if (uiEvent is ErrorEvent) {
            snackbarHostState.showSnackbar(uiEvent.message)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {
        SwipeRefresh(state = rememberSwipeRefreshState(state.isRefreshing), onRefresh = onRefresh) {
            // box wrapper makes swipe refresh smooth
            Box(Modifier.fillMaxSize()) {
                FeedList(
                    list = state.list,
                    lastPosition = state.lastPosition,
                    uiEvent = uiEvent,
                    onItemClick = onItemClick,
                    onListSettle = onListSettle
                )
            }
        }
    }
}

@Composable
fun FeedList(
    list: List<FeedItem>,
    lastPosition: Pair<Int, Int>,
    uiEvent: UiEvent,
    onItemClick: (FeedItem) -> Unit,
    onListSettle: (String, Int) -> Unit
) {
    if (list.isNotEmpty()) {
        val state = rememberLazyListState(
            lastPosition.first.coerceAtLeast(0),
            lastPosition.second.coerceAtLeast(0)
        )

        LaunchedEffect(key1 = uiEvent) {
            if (uiEvent is ScrollEvent) {
                state.animateScrollBy(-16.dp.value)
            }
        }

        LaunchedEffect(key1 = state.isScrollInProgress) {
            if (state.isScrollInProgress.not()) {
                onListSettle(
                    list[state.firstVisibleItemIndex].id,
                    state.firstVisibleItemScrollOffset
                )
            }
        }

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {
            items(items = list, key = { it.id }) {
                Item(item = it, onItemClick)
            }
        }
    }
}

@Composable
fun Item(item: FeedItem, onItemClick: (FeedItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { onItemClick(item) }
            }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
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
    }
}
