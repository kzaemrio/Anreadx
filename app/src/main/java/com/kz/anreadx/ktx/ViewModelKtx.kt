package com.kz.anreadx.ktx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

inline fun ViewModel.launch(crossinline block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch { block() }
}
