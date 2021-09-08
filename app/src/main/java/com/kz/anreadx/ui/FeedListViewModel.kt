package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.launch
import com.kz.anreadx.ktx.map
import com.kz.anreadx.model.Feed
import com.kz.anreadx.repository.FeedListRepository
import com.kz.anreadx.ui.UiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private val store = UiState().asStore()

    val uiStateFlow: StateFlow<UiState>
        get() = store.flow

    init {
        updateList()
    }

    fun updateList() {
        launch {
            store.isRefreshing { true }
            store.errorMessage { NO_ERROR }
            try {
                repository.update()
            } catch (e: Exception) {
                store.errorMessage { e.message ?: NO_ERROR }
            }
            val feedList: List<Feed> = repository.getList()
            val feedItemList = withContext(cpu) { feedList.map { FeedItem(feed = this) } }
            store.list { feedItemList }
            store.isRefreshing { false }
        }
    }

    fun clearAll() {
        launch { repository.readAll() }

        launch {
            store.isRefreshing { true }
            store.list { map { copy(done = true) } }
            store.isRefreshing { false }
        }
    }

    fun read(item: FeedItem) {
        launch { repository.read(item.id) }
        launch {
            store.list { map { copy(done = if (id == item.id) true else done) } }
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
