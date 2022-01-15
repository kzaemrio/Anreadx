package com.kz.anreadx.ui

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.map
import com.kz.anreadx.model.Feed
import com.kz.anreadx.model.LastPosition
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun FeedList(
    onItemClick: (String) -> Unit,
    viewModel: FeedListViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val readAll: () -> Unit = remember(scope) {
        {
            scope.launch {
                viewModel.feedDao.readAll()
            }
        }
    }

    val refresh: () -> Unit = remember(scope, scaffoldState) {
        {
            scope.launch {
                isRefreshing = true
                val feeds: List<Feed> = try {
                    viewModel.requestList()
                } catch (e: Exception) {
                    scaffoldState.snackbarHostState.showSnackbar(
                        e.message ?: "something wrong"
                    )
                    emptyList()
                }
                viewModel.feedDao.insert(feeds)
                isRefreshing = false
            }
        }
    }

    LaunchedEffect("init") {
        refresh()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { readAll();refresh() }) {
            // box wrapper makes swipe refresh smooth
            Box(Modifier.fillMaxSize()) {
                LazyFeedList(onItemClick = onItemClick)
            }
        }
    }
}

@Composable
fun LazyFeedList(
    list: List<FeedItem> = list(),
    onItemClick: (String) -> Unit,
    viewModel: FeedListViewModel = viewModel()
) {
    if (list.isNotEmpty()) {
        val (index, offset) = lastPosition(list)
        if (index >= 0) {

            val state = rememberLazyListState(index, offset)

            LaunchedEffect(list) {
                state.animateScrollBy(-16.dp.value)
            }

            LaunchedEffect(state.isScrollInProgress) {
                if (state.isScrollInProgress.not()) {
                    viewModel.lastPositionDao.insert(
                        LastPosition(
                            list[state.firstVisibleItemIndex].id,
                            state.firstVisibleItemScrollOffset
                        )
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
}

@Composable
fun Item(item: FeedItem, onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { onItemClick(item.id) }
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


@Composable
fun lastPosition(list: List<FeedItem>, viewModel: FeedListViewModel = viewModel()): Pair<Int, Int> {
    var result by remember {
        mutableStateOf(-1 to -1)
    }

    LaunchedEffect("init") {
        result = viewModel.lastPositionDao.query()
            ?.run { list.indexOfFirst { it.id == link } to offset }
            ?.run {
                if (first >= 0) {
                    this
                } else {
                    null
                }
            } ?: 0 to 0
    }

    return result
}

@Composable
fun list(viewModel: FeedListViewModel = viewModel()): List<FeedItem> {
    var result: List<FeedItem> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect("init") {
        viewModel.feedDao.listFlow()
            .map { it.map { FeedItem(this) } }
            .onEach { result = it }
            .flowOn(viewModel.background).launchIn(this)
    }

    return result
}
