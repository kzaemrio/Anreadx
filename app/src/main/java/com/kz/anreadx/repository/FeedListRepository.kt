package com.kz.anreadx.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.dispatcher.IO
import com.kz.anreadx.model.Feed
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class FeedListRepository constructor(
    private val db: DB,
    private val io: IO,
    private val feedDao: FeedDao,
    private val rssService: RssService
) {
    suspend fun getList(): List<Feed> {
        return withContext(db) {
            feedDao.getAll()
        }
    }

    suspend fun update() {
        withContext(db) {
            feedDao.deleteBefore(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
            )
            feedDao.insert(request())
        }
    }

    suspend fun readAll() {
        withContext(db) {
            feedDao.clearAll()
        }
    }

    private suspend fun request(): List<Feed> = withContext(io) {
        when (val response = rssService.request()) {
            is NetworkResponse.Success -> response.body.channel.feedList
            is NetworkResponse.ServerError -> throw response.error
            is NetworkResponse.NetworkError -> throw response.error
            is NetworkResponse.UnknownError -> throw response.error
        }
    }
}
