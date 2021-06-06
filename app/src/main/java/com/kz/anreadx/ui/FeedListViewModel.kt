package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.map
import com.kz.anreadx.model.Feed
import com.kz.anreadx.repository.FeedListRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private var isRefreshing = MutableStateFlow(false)

    private var list = MutableStateFlow(emptyList<FeedItem>())

    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    val uiStateFlow: StateFlow<UiState>
        get() = _uiStateFlow

    init {
        viewModelScope.launch {
            combine(isRefreshing, list, ::UiState).collect {
                _uiStateFlow.value = it
            }
        }
        updateList()
    }

    private fun process(getFeedList: suspend () -> List<Feed>) = viewModelScope.launch {
        isRefreshing.emit(true)
        val feedList = getFeedList()
        val itemList = withContext(cpu) {
            feedList.map { FeedItem(it) }
        }
        list.emit(itemList)
        isRefreshing.emit(false)
    }

    fun updateList() {
        process(repository::updateAndGet)
    }

    fun clearAll() {
        process(repository::readAllAndGet)
    }

    fun read(link: String) {
        process { repository.readAndGet(link) }
    }
}

data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList()
)
