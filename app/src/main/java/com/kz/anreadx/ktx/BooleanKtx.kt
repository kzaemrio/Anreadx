package com.kz.anreadx.ktx

inline fun Boolean.ifTrue(action: () -> Unit) {
    if (this) {
        action()
    }
}

inline fun Boolean.ifFalse(action: () -> Unit) {
    if (!this) {
        action()
    }
}
