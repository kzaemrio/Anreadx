package com.kz.anreadx.di

inline fun <reified P1, reified P2, reified P3, reified R> ((P1, P2, P3) -> R).curry(): (P1) -> (P2) -> (P3) -> R {
    return { p1 -> { p2 -> { p3 -> this(p1, p2, p3) } } }
}

inline fun <reified P1, reified P2, reified P3, reified P4, reified R> ((P1, P2, P3, P4) -> R).curry(): (P1) -> (P2) -> (P3) -> (P4) -> R {
    return { p1 -> { p2 -> { p3 -> { p4 -> this(p1, p2, p3, p4) } } } }
}

inline fun <reified P1, reified P2, reified R> ((P1, P2) -> R).curry(): (P1) -> (P2) -> R {
    return { p1 -> { p2 -> this(p1, p2) } }
}
