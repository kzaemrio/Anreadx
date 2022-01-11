package com.kz.anreadx

import android.app.Application
import com.kz.anreadx.di.ExecutorEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

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
        }
    }
}
