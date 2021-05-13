package com.kz.anreadx.ktx

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.reflect.KProperty

inline fun <T> state(init: () -> T) = mutableStateOf(init())

operator fun <T> MutableState<T>.getValue(thisRef: Any, property: KProperty<*>): T = value

operator fun <T> MutableState<T>.setValue(thisRef: Any, property: KProperty<*>, value: T) {
    this.value = value
}
