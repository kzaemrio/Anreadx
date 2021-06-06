package com.kz.anreadx.di

import androidx.room.Room
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.dispatcher.IO
import com.kz.anreadx.model.RssXmlConverter
import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.model.RssXmlParser
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.AppDatabase
import com.kz.anreadx.repository.FeedListRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.create
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

private val xml = DI.Module("xml") {
    bindSingleton { RssXmlParser() }

    bindSingleton { RssXmlConverter(instance()) }

    bindSingleton { RssXmlFactory(instance()) }
}

private val retrofit = DI.Module("retrofit") {
    bindSingleton<RssService> {
        Retrofit.Builder()
            .baseUrl("https://www.ithome.com")
            .client(instance())
            .addConverterFactory(instance())
            .build()
            .create()
    }

    bindSingleton {
        OkHttpClient.Builder()
            .addNetworkInterceptor(instance<Interceptor>())
            .build()
    }

    bindSingleton<Interceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }
}

val di: DI.MainBuilder.() -> Unit = {
    importOnce(dispatcher)
    importOnce(room)
    importOnce(xml)
    importOnce(retrofit)
    bindSingleton { FeedListRepository(instance(), instance(), instance(), instance()) }
}
