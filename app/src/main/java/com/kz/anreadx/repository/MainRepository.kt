package com.kz.anreadx.repository

import com.kz.anreadx.dispatcher.DispatcherSwitch
import com.kz.anreadx.model.Feed
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.FeedDao
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dispatcher: DispatcherSwitch,
    private val feedDao: FeedDao,
    private val rssService: RssService
) {
    suspend fun updateAndGet(): List<Feed> = actionAndGet(this::update)

    suspend fun readAllAndGet(): List<Feed> = actionAndGet(this::readAll)

    suspend fun readAndGet(link: String): List<Feed> = actionAndGet { read(link) }

    private suspend fun actionAndGet(action: suspend () -> Unit): List<Feed> {
        action.invoke()
        return dispatcher.db { feedDao.getAll() }
    }

    private suspend fun read(link: String) = dispatcher.db {
        feedDao.read(link)
    }

    private suspend fun readAll() = dispatcher.db {
        feedDao.clearAll()
    }

    private suspend fun update() {
        val list = requestOnline()
        insert(list)
        deleteOld()
    }

    private suspend fun deleteOld() = dispatcher.db {
        feedDao.deleteBefore(
            System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)
        )
    }

    private suspend fun insert(list: List<Feed>) = dispatcher.db {
        feedDao.insert(list)
    }

    private suspend fun requestOnline() = dispatcher.io {
        rssService.request().channel.feedList
    }
}
