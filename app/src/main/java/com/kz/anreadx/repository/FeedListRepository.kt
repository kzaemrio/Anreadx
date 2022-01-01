package com.kz.anreadx.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.dispatcher.IO
import com.kz.anreadx.model.Feed
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FeedListRepository @Inject constructor(
    private val db: DB,
    private val io: IO,
    private val feedDao: FeedDao,
    private val rssService: RssService
) {

    init {
        CoroutineScope(db).launch {
            feedDao.deleteBefore(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
            )
        }
    }

    suspend fun localList(): List<Feed> = withContext(db) {
        feedDao.list()
    }

    suspend fun refresh(): Unit = withContext(db) {
        feedDao.insert(requestList())
    }

    suspend fun readAll(): Unit = withContext(db) {
        feedDao.readAll()
    }

    suspend fun read(id: String): Unit = withContext(db) {
        feedDao.read(id)
    }

    private suspend fun requestList(): List<Feed> = withContext(io) {
        when (val response = rssService.rss()) {
            is NetworkResponse.Success -> response.body.channel.feedList
            is NetworkResponse.ServerError -> throw response.error
            is NetworkResponse.NetworkError -> throw response.error
            is NetworkResponse.UnknownError -> throw response.error
        }
    }
}
