package com.kz.anreadx

import android.app.Application
import coil.Coil
import com.kz.anreadx.di.ExecutorEntryPoint
import com.kz.anreadx.di.ImageLoaderEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp

lateinit var coil_image_path: String

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        EntryPointAccessors.fromApplication<ExecutorEntryPoint>(this).executor().execute {
            coil_image_path = cacheDir.apply { mkdirs() }.resolve("coil_image_cache").absolutePath

            Coil.setImageLoader(factory = {
                EntryPointAccessors.fromApplication<ImageLoaderEntryPoint>(this).imageLoader()
            })
        }
    }
}
