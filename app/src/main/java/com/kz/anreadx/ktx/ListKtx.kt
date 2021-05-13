package com.kz.anreadx.ktx

inline fun <T, R> List<T>.map(transform: (T) -> R): List<R> = ArrayList<R>(size).also {
    forEach { item -> it.add(transform(item)) }
}
