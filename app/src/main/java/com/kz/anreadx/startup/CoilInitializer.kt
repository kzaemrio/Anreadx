package com.kz.anreadx.startup

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import com.kz.anreadx.di.diExecutor
import com.kz.anreadx.di.diImageLoader
import java.util.*

class CoilInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        context.diExecutor.execute {
            Coil.setImageLoader(context.diImageLoader)
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = Collections.emptyList()
}
