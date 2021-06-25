package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.launch
import com.kz.anreadx.ktx.map
import com.kz.anreadx.ktx.reduce
import com.kz.anreadx.model.Feed
import com.kz.anreadx.repository.FeedListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    private val list = MutableStateFlow(emptyList<FeedItem>())

    private val errorMessage = MutableStateFlow(NO_ERROR)

    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    val uiStateFlow: StateFlow<UiState>
        get() = _uiStateFlow

    init {
        launch {
            combine(isRefreshing, list, errorMessage, ::UiState).collect {
                _uiStateFlow.emit(it)
            }
        }
        updateList()
    }

    fun updateList() {
        launch {
            isRefreshing.reduce { true }
            errorMessage.reduce { NO_ERROR }
            try {
                repository.update()
            } catch (e: Exception) {
                errorMessage.reduce { e.message ?: NO_ERROR }
            }
            val feedList: List<Feed> = repository.getList()
            val feedItemList = withContext(cpu) { feedList.map { FeedItem(feed = this) } }
            list.reduce { feedItemList }
            isRefreshing.reduce { false }
        }
    }

    fun clearAll() {
        launch { repository.readAll() }

        isRefreshing.reduce { true }
        list.reduce { map { copy(done = true) } }
        isRefreshing.reduce { false }
    }

    fun read(item: FeedItem) {
        launch { repository.read(item.id) }
        list.reduce { map { copy(done = if (id == item.id) true else done) } }
    }
}

data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
    val errorMessage: String = NO_ERROR
)

private const val NO_ERROR = ""
