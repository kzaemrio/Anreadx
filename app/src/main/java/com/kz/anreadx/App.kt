package com.kz.anreadx

import android.app.Application
import coil.Coil
import com.kz.anreadx.di.ExecutorEntryPoint
import com.kz.anreadx.di.ImageLoaderEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        EntryPointAccessors.fromApplication<ExecutorEntryPoint>(this).executor().execute {
            Coil.setImageLoader(
                EntryPointAccessors.fromApplication<ImageLoaderEntryPoint>(this).imageLoader()
            )
        }
    }
}
