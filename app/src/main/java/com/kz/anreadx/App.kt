package com.kz.anreadx

import android.app.Application
import com.kz.anreadx.di.ExecutorEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

lateinit var coil_image_path: String

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        EntryPointAccessors.fromApplication<ExecutorEntryPoint>(this).executor().execute {
            Dispatchers.Main.apply {
                // MainDispatcherLoader.loadMainDispatcher()
            }

            Job.apply {
                // initializeDefaultExceptionHandlers()
            }

            coil_image_path = cacheDir.apply { mkdirs() }.resolve("coil_image_cache").absolutePath
        }
    }
}
