package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.combine
import com.kz.anreadx.ktx.then
import kotlinx.coroutines.CoroutineScope

@Composable
fun FeedList(
    navToDetail: (String) -> Unit,
    viewModel: FeedListViewModel = vmKodein(::FeedListViewModel)
) {
    val state = viewModel.uiStateFlow.collectAsState().value
    val event = viewModel.uiEventFlow.collectAsState(initial = Nop).value

    FeedList(
        state = state,
        uiEvent = event,
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
    onListSettle: (Int, Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffectWhen(condition = uiEvent is ErrorEvent) {
        snackbarHostState.showSnackbar((uiEvent as ErrorEvent).message)
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
    onListSettle: (Int, Int) -> Unit
) {
    if (list.isNotEmpty()) {
        val state = rememberLazyListState(
            lastPosition.first.coerceAtLeast(0),
            lastPosition.second.coerceAtLeast(0)
        )

        LaunchedEffectWhen(condition = uiEvent is ScrollEvent) {
            state.animateScrollBy(-16.dp.value)
        }

        LaunchedEffectWhen(state.isScrollInProgress.not()) {
            onListSettle(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { onItemClick(item) }
            }
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

@Composable
fun LaunchedEffectWhen(condition: Boolean, block: suspend CoroutineScope.() -> Unit) {
    if (condition) {
        LaunchedEffect(Unit) {
            block()
        }
    }
}
