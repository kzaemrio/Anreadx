package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.model.Feed
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.persistence.LastPositionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    val background: Background,
    val feedDao: FeedDao,
    val lastPositionDao: LastPositionDao,
    private val rssService: RssService
) : ViewModel() {

    init {
        viewModelScope.launch {
            feedDao.deleteBefore(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
            )
        }
    }

    suspend fun requestList(): List<Feed> = when (val response = rssService.rss()) {
        is NetworkResponse.Success -> response.body.channel.feedList
        is NetworkResponse.ServerError -> throw response.error
        is NetworkResponse.NetworkError -> throw response.error
        is NetworkResponse.UnknownError -> throw response.error
    }
}
