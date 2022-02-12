package com.kz.anreadx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kz.anreadx.ui.FeedDetail
import dagger.hilt.android.AndroidEntryPoint


const val EXTRA_LINK = "EXTRA_LINK"

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackClick = ::onBackPressed

        setContent {
            FeedDetail(onBackClick)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
