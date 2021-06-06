package com.kz.anreadx.repository

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
    suspend fun updateAndGet(): List<Feed> = actionAndGet(this::update)

    suspend fun readAllAndGet(): List<Feed> = actionAndGet(this::readAll)

    suspend fun readAndGet(link: String): List<Feed> = actionAndGet { read(link) }

    private suspend fun actionAndGet(action: suspend () -> Unit): List<Feed> {
        action.invoke()
        return withContext(db) { feedDao.getAll() }
    }

    private suspend fun read(link: String) = withContext(db) {
        feedDao.read(link)
    }

    private suspend fun readAll() = withContext(db) {
        feedDao.clearAll()
    }

    private suspend fun update() {
        val list = requestOnline()
        insert(list)
        deleteOld()
    }

    private suspend fun deleteOld() = withContext(db) {
        feedDao.deleteBefore(
            System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
        )
    }

    private suspend fun insert(list: List<Feed>) = withContext(db) {
        feedDao.insert(list)
    }

    private suspend fun requestOnline() = withContext(io) {
        rssService.request().channel.feedList
    }
}
