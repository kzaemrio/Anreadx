package com.kz.anreadx.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.AppDatabase
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.persistence.LastPositionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor {
            Thread(it, "db-single-thread")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, executor: Executor): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "room-db"
        ).setQueryExecutor(executor).setTransactionExecutor(executor).build()
    }

    @Provides
    @Singleton
    fun provideFeedDao(appDatabase: AppDatabase): FeedDao {
        return appDatabase.itemDao()
    }

    @Provides
    @Singleton
    fun provideLastPositionDao(appDatabase: AppDatabase): LastPositionDao {
        return appDatabase.lastPositionDao()
    }

    @Provides
    @Singleton
    fun provideRssService(
        client: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory
    ): RssService {
        return Retrofit.Builder()
            .baseUrl("https://www.ithome.com")
            .client(client)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(converterFactory)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(fac: RssXmlFactory): Converter.Factory {
        return fac
    }

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return NetworkResponseAdapterFactory()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context, okHttpClient: OkHttpClient): ImageLoader {
        return ImageLoader.Builder(context).okHttpClient(okHttpClient = okHttpClient).build()
    }
}
