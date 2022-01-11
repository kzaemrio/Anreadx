package com.kz.anreadx.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.model.Feed
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FeedListRepository @Inject constructor(
    background: Background,
    private val feedDao: FeedDao,
    private val rssService: RssService
) {

    init {
        CoroutineScope(background).launch {
            feedDao.deleteBefore(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
            )
        }
    }

    suspend fun localList(): List<Feed> = feedDao.list()

    suspend fun refresh(): Unit = feedDao.insert(requestList())

    suspend fun readAll(): Unit = feedDao.readAll()

    suspend fun read(id: String): Unit = feedDao.read(id)

    private suspend fun requestList(): List<Feed> = when (val response = rssService.rss()) {
        is NetworkResponse.Success -> response.body.channel.feedList
        is NetworkResponse.ServerError -> throw response.error
        is NetworkResponse.NetworkError -> throw response.error
        is NetworkResponse.UnknownError -> throw response.error
    }
}
