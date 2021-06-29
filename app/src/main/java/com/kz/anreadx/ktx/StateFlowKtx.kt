package com.kz.anreadx.ktx

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * data class TodoItem(val text: String, val done: Boolean)
 * val flow = MutableStateFlow(TodoItem("study", false))
 * flow.reduce { copy(done = true) } // flow.value = flow.value.copy(done = true)
 */
inline fun <T> MutableStateFlow<T>.reduce(
    transform: T.() -> T
) {
    value = value.transform()
}
