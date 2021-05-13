package com.kz.anreadx.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kz.anreadx.R
import com.kz.anreadx.di.FeedDaoEntryPoint
import com.kz.anreadx.model.Feed
import com.kz.anreadx.model.detailTimeLabel
import dagger.hilt.android.EntryPointAccessors


const val EXTRA_LINK = "EXTRA_LINK"

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        EntryPointAccessors.fromApplication(
            applicationContext,
            FeedDaoEntryPoint::class.java
        ).feedDao().query(intent.getStringExtra(EXTRA_LINK)!!).observe(this, ::bind)
    }

    private fun bind(feed: Feed) {
        SwipeRefreshLayout(this).apply {
            WebView(this@DetailActivity).also {
                it.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        isRefreshing = true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isRefreshing = false
                        isEnabled = false
                    }
                }
                render(it, feed)
                addView(it)
            }
            setContentView(this)
        }
    }

    private fun render(web: WebView, item: Feed) {
        val pubDate = item.detailTimeLabel()
        val html = """
            <link rel="stylesheet" type="text/css" href="style.css" />
            <h3>${item.title}</h3>
            <p/>${
            String.format(
                "<p style=\"color:#%06X\">",
                0xFFFFFF and Color.LTGRAY
            )
        }$pubDate</p>
            <p/>${item.description}
            """.trimIndent()
        web.loadDataWithBaseURL(
            "file:///android_asset/",
            html,
            "text/html",
            "UTF-8",
            null
        )
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> true.apply { finish() }
            else -> super.onMenuItemSelected(featureId, item)
        }
    }
}

fun Context.launchDetail(link: String) {
    Intent(this, DetailActivity::class.java).apply {
        putExtra(EXTRA_LINK, link)
        startActivity(this)
    }
}
