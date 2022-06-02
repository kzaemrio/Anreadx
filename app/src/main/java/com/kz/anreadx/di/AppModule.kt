package com.kz.anreadx.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.kz.anreadx.coil_image_path
import com.kz.anreadx.dispatcher.Background
import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.network.RssService
import com.kz.anreadx.persistence.AppDatabase
import com.kz.anreadx.persistence.FeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun bindExecutorService(executor: Executor): ExecutorService = executor as ExecutorService

    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor {
            Thread(it, "bg-single-thread")
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
    fun provideOkHttpClient(
        executorService: ExecutorService,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .dispatcher(Dispatcher(executorService))
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

    @OptIn(ExperimentalCoilApi::class)
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        diskCache: DiskCache,
        background: Background
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient = okHttpClient)
            .diskCache(diskCache)
            .dispatcher(background)
            .build()
    }

    @OptIn(ExperimentalCoilApi::class)
    @Provides
    @Singleton
    fun provideDiskCache(
        background: Background
    ): DiskCache {
        return DiskCache.Builder()
            .directory(File(coil_image_path))
            .cleanupDispatcher(background)
            .maxSizeBytes(256_000_000L) // maxSizePercent cause blocking by StatFs
            .build()
    }
}
