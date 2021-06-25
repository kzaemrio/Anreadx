package com.kz.anreadx.ktx

import kotlinx.coroutines.flow.MutableStateFlow

inline fun <T> MutableStateFlow<T>.reduce(transform: T.() -> T) {
    value = value.transform()
}
