package com.kz.anreadx.di

import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.network.RssService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun rssService(retrofit: Retrofit): RssService = retrofit.create()

    @Singleton
    @Provides
    fun retrofit(client: OkHttpClient, rssXmlFactory: RssXmlFactory): Retrofit = Retrofit.Builder()
            .baseUrl("https://www.ithome.com")
            .client(client)
            .addConverterFactory(rssXmlFactory)
            .build()

    @Singleton
    @Provides
    fun client(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun loggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
}
