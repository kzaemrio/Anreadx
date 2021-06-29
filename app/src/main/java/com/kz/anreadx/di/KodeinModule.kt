package com.kz.anreadx.di

import androidx.room.Room
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.dispatcher.IO
import com.kz.anreadx.persistence.AppDatabase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val dispatcher = DI.Module(name = "dispatcher") {
    bindSingleton { DB(instance()) }
    bindSingleton { IO() }
    bindSingleton { CPU() }
    bindSingleton<Executor> {
        Executors.newSingleThreadExecutor {
            Thread(it, "db-single-thread")
        }
    }
}

private val room = DI.Module(name = "room") {
    bindSingleton { instance<AppDatabase>().itemDao() }
    bindSingleton {
        Room.databaseBuilder(
            instance(),
            AppDatabase::class.java,
            "room-db"
        ).setQueryExecutor(instance()).setQueryExecutor(instance()).build()
    }
}

val routerDi: DI.MainBuilder.() -> Unit = {
    importOnce(dispatcher)
    importOnce(room)
}
