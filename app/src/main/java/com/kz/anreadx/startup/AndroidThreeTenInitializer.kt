package com.kz.anreadx.startup

import android.content.Context
import androidx.startup.Initializer
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kz.anreadx.di.diExecutor
import java.util.*

class AndroidThreeTenInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        context.diExecutor.execute {
            AndroidThreeTen.init(context)
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = Collections.emptyList()
}
