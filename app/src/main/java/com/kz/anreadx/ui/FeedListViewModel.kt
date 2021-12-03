package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.map
import com.kz.anreadx.repository.FeedListRepository
import com.kz.anreadx.ui.UiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private val store = UiState().asStore()

    val uiStateFlow: StateFlow<UiState>
        get() = store.flow

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            store.isRefreshing { true }
            store.errorMessage { NO_ERROR }

            try {
                repository.refresh()
            } catch (e: Exception) {
                store.errorMessage { e.message ?: NO_ERROR }
            }

            val feedItemList = repository.localList().map { feed ->
                async(cpu) { FeedItem(feed) }
            }.awaitAll()

            store.list { feedItemList }
            store.isRefreshing { false }
        }
    }

    fun onReadAll() {
        viewModelScope.launch { repository.readAll() }

        viewModelScope.launch {
            store.isRefreshing { true }
            store.list { map { copy(done = true) } }
            store.isRefreshing { false }
        }
    }

    fun onFeedItemClick(feedItem: FeedItem) {
        viewModelScope.launch { repository.read(feedItem.id) }
        viewModelScope.launch {
            store.list { map { copy(done = if (id == feedItem.id) true else done) } }
        }
    }
}

@FlowStore
data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
    val errorMessage: String = NO_ERROR
)

private const val NO_ERROR = ""
