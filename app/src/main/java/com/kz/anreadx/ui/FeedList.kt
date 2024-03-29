package com.kz.anreadx.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kz.anreadx.R
import com.kz.anreadx.ui.destinations.FeedDetailDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun FeedList(
    navigator: DestinationsNavigator, viewModel: FeedListViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()

    val errorMessageFlow: Flow<RefreshErrorEvent> = viewModel.errorMessageFlow

    LaunchedEffect("init") {
        errorMessageFlow.onEach {
            // *launch* makes showSnackbar no blocking
            launch { scaffoldState.snackbarHostState.showSnackbar(it.message) }
        }.launchIn(this)
    }

    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }, actions = {
            IconButton(onClick = { viewModel.readAll() }) {
                Icon(
                    imageVector = Icons.Rounded.DoneAll,
                    contentDescription = stringResource(id = R.string.menu_clear_all)
                )
            }
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(id = R.string.refresh)
                )
            }
        })
    }) {

        val uiState by viewModel.stateFlow.collectAsState()

        val onItemClick: (String) -> Unit by rememberUpdatedState(newValue = {
            navigator.navigate(FeedDetailDestination(it))
        })

        val refreshing = uiState.isRefreshing

        val state = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = { viewModel.refresh() }
        )

        Box(
            Modifier
                .fillMaxSize()
                .pullRefresh(state)
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
        ) {
            LazyFeedList(
                list = uiState.list,
                onItemClick = onItemClick,
            )

            PullRefreshIndicator(
                refreshing = refreshing,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun LazyFeedList(
    list: List<FeedItem>,
    onItemClick: (String) -> Unit,
) {
    val state = rememberSaveable(inputs = arrayOf(list.size), saver = LazyListState.Saver) {
        LazyListState(0, 0)
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

@Composable
fun Item(item: FeedItem, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures { onItemClick(item.id) }
        }
        .padding(horizontal = 14.dp, vertical = 6.dp)) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = if (item.done) Color.LightGray else Color.DarkGray
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.timeLabel, style = MaterialTheme.typography.body2, color = Color.LightGray
        )
    }
}
