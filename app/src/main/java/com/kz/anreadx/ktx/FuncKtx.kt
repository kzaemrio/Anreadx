package com.kz.anreadx.ktx

inline fun <T1, T2, R> ((T1) -> T2).then(crossinline next: (T2) -> R): (T1) -> R {
    return { next(invoke(it)) }
}

inline fun <T, R> combine(crossinline f1: (T) -> R, crossinline f2: (T) -> R): (T) -> R {
    return {
        f1(it)
        f2(it)
    }
}
