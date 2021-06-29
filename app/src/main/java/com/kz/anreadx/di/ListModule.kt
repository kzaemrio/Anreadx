package com.kz.anreadx.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.kz.anreadx.model.RssXmlConverter
import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.model.RssXmlParser
import com.kz.anreadx.network.RssService
import com.kz.anreadx.repository.FeedListRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.create


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
            .addCallAdapterFactory(instance())
            .addConverterFactory(instance())
            .build()
            .create()
    }

    bindSingleton { NetworkResponseAdapterFactory() }

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

val listDi: DI.MainBuilder.() -> Unit = {
    importOnce(xml)
    importOnce(retrofit)
    bindSingleton { FeedListRepository(instance(), instance(), instance(), instance()) }
}
