package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kz.anreadx.di.curry
import org.kodein.di.compose.rememberInstance


@JvmName("vmKodeinP3")
@Composable
inline fun <reified P1, reified P2, reified P3, reified VM : ViewModel> vmKodein(block: (P1, P2, P3) -> VM): VM {
    return vmKodein(block.curry())
}

@JvmName("vmKodeinP2")
@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1, P2) -> VM): VM {
    return vmKodein(block.curry())
}

@JvmName("vmKodeinP1P2P3")
@Composable
inline fun <reified P1, reified P2, reified P3, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> (P3) -> VM): VM {
    return vmKodein(block.factory())
}

@JvmName("vmKodeinP1P2")
@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> VM): VM {
    return vmKodein(block.factory())
}

@JvmName("vmKodeinP1")
@Composable
inline fun <reified P1, reified VM : ViewModel> vmKodein(block: (P1) -> VM): VM {
    return vmKodein(block.factory())
}

@JvmName("vmKodeinP1P2P3Factory")
@Composable
inline fun <reified P1, reified P2, reified P3, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> (P3) -> () -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinP1P2Factory")
@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> () -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinP1Factory")
@Composable
inline fun <reified P1, reified VM : ViewModel> vmKodein(block: (P1) -> () -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@Composable
inline fun <reified VM : ViewModel> vmKodein(crossinline block: () -> VM): VM {
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return block() as T
        }
    })
}

@JvmName("vmKodeinFactory3")
inline fun <reified P1, reified P2, reified P3, reified R> ((P1) -> (P2) -> (P3) -> R).factory(): (P1) -> (P2) -> (P3) -> () -> R {
    return { p1 -> { p2 -> { p3 -> { this(p1)(p2)(p3) } } } }
}

@JvmName("vmKodeinFactory2")
inline fun <reified P1, reified P2, reified R> ((P1) -> (P2) -> R).factory(): (P1) -> (P2) -> () -> R {
    return { p1 -> { p2 -> { this(p1)(p2) } } }
}

@JvmName("vmKodeinFactory1")
inline fun <reified P1, reified R> ((P1) -> R).factory(): (P1) -> () -> R {
    return { p1 -> { this(p1) } }
}
