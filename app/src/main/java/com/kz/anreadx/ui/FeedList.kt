package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.combine
import com.kz.anreadx.ktx.ifTrue
import com.kz.anreadx.ktx.then
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun FeedList(
    navToDetail: (String) -> Unit,
    viewModel: FeedListViewModel = vmKodein(::FeedListViewModel)
) {
    FeedList(
        state = viewModel.uiStateFlow.collectAsState().value,
        scrollEventFlow = viewModel.scrollEventFlow,
        onRefresh = viewModel::onRefresh,
        onClear = viewModel::onReadAll,
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
    scrollEventFlow: Flow<Unit>,
    onRefresh: () -> Unit,
    onClear: () -> Unit,
    onItemClick: (FeedItem) -> Unit,
    onListSettle: (Int, Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState

    val errorMessage = state.errorMessage
    LaunchedEffect(errorMessage) {
        errorMessage.isNotBlank().ifTrue {
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
            // box wrapper makes swipe refresh smooth
            Box(Modifier.fillMaxSize()) {
                FeedList(
                    list = state.list,
                    lastPosition = state.lastPosition,
                    scrollEventFlow = scrollEventFlow,
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
    scrollEventFlow: Flow<Unit>,
    onItemClick: (FeedItem) -> Unit,
    onListSettle: (Int, Int) -> Unit
) {
    if (list.isNotEmpty()) {
        val state = rememberLazyListState(
            lastPosition.first.coerceAtLeast(0),
            lastPosition.second.coerceAtLeast(0)
        )

        LaunchedEffect(Unit) {
            scrollEventFlow.onEach {
                delay(200)
                state.animateScrollBy(-16.dp.value)
            }.launchIn(this)
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

@Composable
fun LaunchedEffectWhen(condition: Boolean, block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(condition) {
        if (condition) {
            block()
        }
    }
}
