package com.kz.anreadx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kz.anreadx.di.routerDi
import com.kz.anreadx.ui.FeedList
import org.kodein.di.android.androidCoreModule
import org.kodein.di.compose.subDI
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navToDetail: (String) -> Unit = {
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("link", it)
            })
        }

        setContent {
            withDI(androidCoreModule(application)) {
                subDI(diBuilder = routerDi) {
                    FeedList(navToDetail = navToDetail)
                }
            }
        }
    }
}
