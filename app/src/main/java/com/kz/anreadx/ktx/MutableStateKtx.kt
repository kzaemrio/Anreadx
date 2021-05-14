package com.kz.anreadx.ktx

import androidx.compose.runtime.mutableStateOf

inline fun <T> state(init: () -> T) = mutableStateOf(init())
