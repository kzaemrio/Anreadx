package com.kz.anreadx.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.ktx.map
import com.kz.anreadx.repository.FeedListRepository
import com.kz.anreadx.repository.LastPositionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val background: Background,
    private val listRepository: FeedListRepository,
    private val lastPositionRepository: LastPositionRepository
) : ViewModel() {

    private var isRefreshing by mutableStateOf(false)
    private var feedItemList by mutableStateOf(emptyList<FeedItem>())
    private var lastPosition by mutableStateOf(NO_LAST_POSITION)

    val uiState by derivedStateOf { UiState(isRefreshing, feedItemList, lastPosition) }

    var uiEvent: UiEvent by mutableStateOf(Nop)
        private set

    init {
        onRefresh(readAll = false)
    }

    fun onRefresh(readAll: Boolean = true) {
        viewModelScope.launch {
            isRefreshing = true

            if (readAll) {
                launch { listRepository.readAll() }
            }

            try {
                listRepository.refresh()
            } catch (e: Exception) {
                uiEvent = ErrorEvent(e.message ?: "something wrong")
            }

            val list = listRepository.localList().map { feed ->
                async(background) { FeedItem(feed) }
            }.awaitAll()

            val position: Pair<Int, Int> = lastPositionRepository.query()
                ?.run { list.indexOfFirst { it.id == link } to offset }
                ?.run {
                    if (first >= 0) {
                        this
                    } else {
                        NO_LAST_POSITION
                    }
                } ?: NO_LAST_POSITION

            feedItemList = list
            lastPosition = position
            isRefreshing = false

            if (position != NO_LAST_POSITION) {
                delay(200)
                uiEvent = ScrollEvent()
            }
        }
    }

    fun onFeedItemClick(feedItem: FeedItem) {
        viewModelScope.launch {
            listRepository.read(feedItem.id)
        }

        viewModelScope.launch {
            feedItemList = feedItemList.map { copy(done = if (id == feedItem.id) true else done) }
        }
    }

    fun onListSettle(link: String, offset: Int) {
        viewModelScope.launch {
            lastPositionRepository.insert(link, offset)
        }
    }
}

data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
    val lastPosition: Pair<Int, Int> = NO_LAST_POSITION,
)

private val NO_LAST_POSITION = -1 to -1

sealed interface UiEvent
object Nop : UiEvent
data class ScrollEvent(val time: Long = System.currentTimeMillis()) : UiEvent
data class ErrorEvent(val message: String) : UiEvent
