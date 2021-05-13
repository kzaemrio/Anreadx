package com.kz.anreadx.startup

import android.content.Context
import androidx.startup.Initializer
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*

class AndroidThreeTenInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AndroidThreeTen.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = Collections.emptyList()
}
