package com.kz.anreadx.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.kz.anreadx.model.Feed
import com.kz.anreadx.persistence.AppDatabase
import com.kz.anreadx.persistence.FeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton
import kotlin.coroutines.Continuation

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Singleton
    @Provides
    fun itemDao(appDatabase: AppDatabase): FeedDao = appDatabase.itemDao()

    @Singleton
    @Provides
    fun appDatabase(application: Application, executor: Executor): AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "room-db"
        ).setQueryExecutor(executor).setQueryExecutor(executor).build()

    @Singleton
    @Provides
    fun executor(): Executor = Executors.newSingleThreadExecutor {
        Thread(it, "db-single-thread")
    }
}