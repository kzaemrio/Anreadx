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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun FeedList(
    onItemClick: (String) -> Unit,
    viewModel: FeedListViewModel = hiltViewModel(),
    errorMessageFlow: Flow<RefreshErrorEvent> = viewModel.errorMessageFlow
) {

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect("init") {
        errorMessageFlow.onEach {
            // *launch* makes showSnackbar no blocking
            launch { scaffoldState.snackbarHostState.showSnackbar(it.message) }
        }.launchIn(this)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {

        val uiState by viewModel.stateFlow.collectAsState()

        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isRefreshing),
            onRefresh = { viewModel.readAll();viewModel.refresh() }) {
            // box wrapper makes swipe refresh smooth
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
            ) {
                LazyFeedList(
                    list = uiState.list,
                    onSaveLastPosition = viewModel::saveLastPosition,
                    onItemClick = onItemClick,
                    viewModel::lastPosition
                )
            }
        }
    }
}

@Composable
fun LazyFeedList(
    list: List<FeedItem>,
    onSaveLastPosition: (String, Int) -> Unit,
    onItemClick: (String) -> Unit,
    queryLastPosition: suspend (List<FeedItem>) -> Pair<Int, Int>
) {
    if (list.isNotEmpty()) {
        val (index, offset) = lastPosition(list, queryLastPosition)
        if (index >= 0) {
            LazyFeedList(
                index = index,
                offset = offset,
                list = list,
                onSaveLastPosition = onSaveLastPosition,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun LazyFeedList(
    index: Int,
    offset: Int,
    list: List<FeedItem>,
    onSaveLastPosition: (String, Int) -> Unit,
    onItemClick: (String) -> Unit,
    scrollEventFlow: Flow<ScrollEvent> = hiltViewModel<FeedListViewModel>().scrollEventFlow
) {
    val state = rememberLazyListState(index, offset)

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        items(items = list, key = { it.id }) {
            Item(item = it, onItemClick)
        }
    }

    LaunchedEffect(key1 = "init") {
        scrollEventFlow.onEach {
            launch { delay(100);state.animateScrollBy(-16.dp.value) }
        }.launchIn(this)
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (state.isScrollInProgress.not()) {
            onSaveLastPosition(
                list[state.firstVisibleItemIndex].id,
                state.firstVisibleItemScrollOffset
            )
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
private fun lastPosition(
    list: List<FeedItem>,
    queryLastPosition: suspend (List<FeedItem>) -> Pair<Int, Int>
): Pair<Int, Int> {
    var result by remember {
        mutableStateOf(-1 to -1)
    }

    LaunchedEffect(key1 = "init") {
        result = queryLastPosition(list)
    }

    return result
}
