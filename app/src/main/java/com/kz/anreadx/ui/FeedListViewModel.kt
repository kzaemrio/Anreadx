package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.map
import com.kz.anreadx.model.Feed
import com.kz.anreadx.repository.FeedListRepository
import com.kz.anreadx.ui.UiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private val store = UiState().asStore()

    val uiStateFlow: StateFlow<UiState>
        get() = store.flow

    @OptIn(ObsoleteCoroutinesApi::class)
    val sendChannel: SendChannel<UiEvent> = viewModelScope.actor {
        for (uiEvent in this) {
            when (uiEvent) {
                is OnFeedItemClick -> {
                    launch { repository.read(uiEvent.feedItem.id) }
                    launch {
                        store.list { map { copy(done = if (id == uiEvent.feedItem.id) true else done) } }
                    }
                }
                OnReadAll -> {
                    launch { repository.readAll() }

                    launch {
                        store.isRefreshing { true }
                        store.list { map { copy(done = true) } }
                        store.isRefreshing { false }
                    }
                }
                OnRefresh -> {
                    launch {
                        store.isRefreshing { true }
                        store.errorMessage { NO_ERROR }
                        try {
                            repository.update()
                        } catch (e: Exception) {
                            store.errorMessage { e.message ?: NO_ERROR }
                        }
                        val feedList: List<Feed> = repository.getList()
                        val feedItemList =
                            withContext(cpu) { feedList.map { FeedItem(feed = this) } }
                        store.list { feedItemList }
                        store.isRefreshing { false }
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            sendChannel.send(OnRefresh)
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
