package com.kz.anreadx.ui

sealed class DetailItem {
    data class Image(val url: String) : DetailItem()
    data class Text(val value: String) : DetailItem()
}
