package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.DispatcherSwitch
import com.kz.anreadx.ktx.getValue
import com.kz.anreadx.ktx.map
import com.kz.anreadx.ktx.setValue
import com.kz.anreadx.ktx.state
import com.kz.anreadx.model.Feed
import com.kz.anreadx.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatcher: DispatcherSwitch,
    private val repository: MainRepository
) : ViewModel() {

    var isLoading by state { false }
        private set

    var list by state { emptyList<ViewItem>() }
        private set

    init {
        updateList()
    }

    private fun process(getFeedList: suspend () -> List<Feed>) = viewModelScope.launch {
        isLoading = true
        val feedList = getFeedList()
        val itemList = dispatcher.cpu {
            feedList.map { ViewItem(it) }
        }
        list = itemList
        isLoading = false
    }

    fun updateList() = process(repository::updateAndGet)

    fun clearAll() = process(repository::readAllAndGet)

    fun read(link: String) = process { repository.readAndGet(link) }
}
