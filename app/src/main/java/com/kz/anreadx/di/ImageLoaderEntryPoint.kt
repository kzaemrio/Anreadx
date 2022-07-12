package com.kz.anreadx.di

import android.content.Context
import coil.ImageLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ImageLoaderEntryPoint {
    fun imageLoader(): ImageLoader
}

inline val Context.diImageLoader: ImageLoader
    get() = EntryPointAccessors.fromApplication<ImageLoaderEntryPoint>(applicationContext)
        .imageLoader()
