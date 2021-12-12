package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.map
import com.kz.anreadx.repository.FeedListRepository
import com.kz.anreadx.repository.LastPositionRepository
import com.kz.anreadx.ui.UiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val listRepository: FeedListRepository,
    private val lastPositionRepository: LastPositionRepository
) : ViewModel() {

    private val store = UiState().asStore()

    val uiStateFlow: StateFlow<UiState>
        get() = store.flow

    private val uiEventChannel = Channel<UiEvent>()

    val uiEventFlow: Flow<UiEvent> = uiEventChannel.receiveAsFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            store.isRefreshing { true }

            try {
                listRepository.refresh()
            } catch (e: Exception) {
                uiEventChannel.send(ErrorEvent(e.message ?: "something wrong"))
            }

            val list = listRepository.localList().map { feed ->
                async(cpu) { FeedItem(feed) }
            }.awaitAll()

            val lastPosition: Pair<Int, Int> = lastPositionRepository.query()
                ?.run { list.indexOfFirst { it.id == link } to offset }
                ?.run {
                    if (first >= 0) {
                        this
                    } else {
                        NO_LAST_POSITION
                    }
                } ?: NO_LAST_POSITION

            store.list { list }
            store.lastPosition { lastPosition }
            store.isRefreshing { false }

            if (lastPosition != NO_LAST_POSITION) {
                delay(200)
                uiEventChannel.send(ScrollEvent)
            }
        }
    }

    fun onReadAll() {
        viewModelScope.launch {
            listRepository.readAll()
        }

        viewModelScope.launch {
            store.list { map { copy(done = true) } }
        }
    }

    fun onFeedItemClick(feedItem: FeedItem) {
        viewModelScope.launch {
            listRepository.read(feedItem.id)
        }

        viewModelScope.launch {
            store.list { map { copy(done = if (id == feedItem.id) true else done) } }
        }
    }

    fun onListSettle(index: Int, offset: Int) {
        val list = uiStateFlow.value.list
        if (list.isNotEmpty()) {
            viewModelScope.launch {
                lastPositionRepository.insert(list[index].id, offset)
            }
        }
    }
}

@FlowStore
data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
    val lastPosition: Pair<Int, Int> = NO_LAST_POSITION,
)

private val NO_LAST_POSITION = -1 to -1

sealed interface UiEvent
object Nop : UiEvent
object ScrollEvent : UiEvent
data class ErrorEvent(val message: String) : UiEvent
