package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.ktx.map
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.ui.FeedListUiStateStore.Companion.asStore
import com.kz.flowstore.annotation.FlowStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ObsoleteCoroutinesApi::class)
@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val background: Background,
    private val feedDao: FeedDao,
    private val rssService: RssService
) : ViewModel() {

    private val store = FeedListUiState().asStore()

    val stateFlow = store.flow

    private val uiEventChannel = BroadcastChannel<UiEvent>(1)

    val errorMessageFlow: Flow<RefreshErrorEvent> =
        uiEventChannel.openSubscription().receiveAsFlow().filterIsInstance()

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
        if (stateFlow.value.isRefreshing.not()) {
            viewModelScope.launch {
                store.isRefreshing { true }
                val list = try {
                    when (val response = rssService.rss()) {
                        is NetworkResponse.Success -> response.body.channel.feedList
                        is NetworkResponse.ServerError -> throw response.error!!
                        is NetworkResponse.NetworkError -> throw response.error
                        is NetworkResponse.UnknownError -> throw response.error
                    }
                } catch (e: Exception) {
                    uiEventChannel.send(RefreshErrorEvent(e.message ?: "refresh error"))
                    emptyList()
                }
                feedDao.insert(list)
                store.isRefreshing { false }
            }
        }
    }

    fun readAll() {
        viewModelScope.launch {
            feedDao.readAll()
        }
    }
}

@FlowStore
data class FeedListUiState(
    val isRefreshing: Boolean = false,
    val list: List<FeedItem> = emptyList(),
)

sealed interface UiEvent
data class RefreshErrorEvent(val message: String) : UiEvent
