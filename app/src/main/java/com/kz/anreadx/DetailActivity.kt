package com.kz.anreadx

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.kz.anreadx.ui.FeedDetail
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


const val EXTRA_LINK = "EXTRA_LINK"

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackClick = { finish() }

        Log.e(TAG, "onCreate: ${imageLoader.hashCode()}")

        setContent {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                FeedDetail(onBackClick)
            }
        }
    }
}

private const val TAG = "DetailActivity"

