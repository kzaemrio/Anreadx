package com.kz.anreadx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.kz.anreadx.di.routerDi
import com.kz.anreadx.ui.FeedDetail
import org.kodein.di.android.androidCoreModule
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI
import org.kodein.di.compose.withDI

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackClick = { finish() }

        setContent {
            withDI(androidCoreModule(application)) {
                subDI(diBuilder = routerDi) {
                    val loader: ImageLoader by rememberInstance()
                    CompositionLocalProvider(LocalImageLoader provides loader) {
                        FeedDetail(onBackClick)
                    }
                }
            }
        }
    }
}
