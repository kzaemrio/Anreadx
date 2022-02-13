package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.ktx.map
import com.kz.anreadx.model.LastPosition
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.persistence.LastPositionDao
import com.kz.anreadx.ui.FeedListUiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val background: Background,
    private val feedDao: FeedDao,
    private val lastPositionDao: LastPositionDao,
    private val rssService: RssService
) : ViewModel() {

    private val store = FeedListUiState().asStore()

    val stateFlow = store.flow

    private val errorMessageChannel = Channel<RefreshErrorEvent>()

    val errorMessageFlow = errorMessageChannel.consumeAsFlow()

    init {
        viewModelScope.launch {
            feedDao.deleteBefore(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
            )

            feedDao.listFlow()
                .map { it.map { FeedItem((this)) } }
                .flowOn(background)
                .onEach { store.list { it } }
                .launchIn(this)

            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            store.isRefreshing { true }
            val list = try {
                when (val response = rssService.rss()) {
                    is NetworkResponse.Success -> response.body.channel.feedList
                    is NetworkResponse.ServerError -> throw response.error
                    is NetworkResponse.NetworkError -> throw response.error
                    is NetworkResponse.UnknownError -> throw response.error
                }
            } catch (e: Exception) {
                errorMessageChannel.send(RefreshErrorEvent(e.message ?: "refresh error"))
                emptyList()
            }
            feedDao.insert(list)
            store.isRefreshing { false }
        }
    }

    fun readAll() {
        viewModelScope.launch {
            feedDao.readAll()
        }
    }

    fun saveLastPosition(id: String, offset: Int) {
        viewModelScope.launch {
            lastPositionDao.insert(LastPosition(id, offset))
        }
    }

    suspend fun lastPosition(list: List<FeedItem>): Pair<Int, Int> = lastPositionDao.query()
        ?.run { list.indexOfFirst { it.id == link } to offset }
        ?.run {
            if (first >= 0) {
                this
            } else {
                null
            }
        } ?: list.lastIndex to 0
}

@FlowStore
data class FeedListUiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
)

data class RefreshErrorEvent(val message: String)
