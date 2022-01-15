package com.kz.anreadx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kz.anreadx.ui.FeedList
import com.kz.anreadx.ui.FeedListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navToDetail: (String) -> Unit = {
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra(EXTRA_LINK, it)
            })
        }

        setContent {
            FeedList(onItemClick = navToDetail)
        }
    }
}
