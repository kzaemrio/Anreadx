package com.kz.anreadx.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FeedListStore {
    var isLoading by mutableStateOf(false)
        private set

    var list by mutableStateOf(emptyList<ViewItem>())
        private set

    fun onRefresh() {

    }

    fun onClearAllClick() {

    }
}
