package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.ktx.ifFalse
import com.kz.anreadx.ktx.map
import com.kz.anreadx.repository.FeedListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedListViewModel constructor(
    private val cpu: CPU,
    private val repository: FeedListRepository
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    private val list = MutableStateFlow(emptyList<FeedItem>())

    private val errorMessage = MutableStateFlow("")

    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    val uiStateFlow: StateFlow<UiState>
        get() = _uiStateFlow

    init {
        viewModelScope.launch {
            combine(isRefreshing, list, errorMessage, ::UiState).collect {
                _uiStateFlow.emit(it)
            }
        }
        updateList()
    }

    private fun process(action: suspend () -> Unit) = viewModelScope.launch {
        isRefreshing.emit(true)
        action()
        val feedItemList = withContext(cpu) {
            repository.getList().map { FeedItem(it) }
        }
        list.emit(feedItemList)
        isRefreshing.emit(false)
    }

    fun updateList() {
        process {
            errorMessage.emit("")
            try {
                repository.update()
            } catch (e: Exception) {
                e.message?.apply {
                    errorMessage.emit(this)
                }
            }
        }
    }

    fun clearAll() {
        process(repository::readAll)
    }

    fun read(item: FeedItem) {
        item.done.ifFalse {
            viewModelScope.launch { repository.read(item.id) }
            list.value.map {
                if (it.id == item.id) {
                    it.copy(done = true)
                } else {
                    it.copy()
                }
            }.apply {
                viewModelScope.launch { list.emit(this@apply) }
            }
        }
    }
}

data class UiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
    val errorMessage: String = ""
)
