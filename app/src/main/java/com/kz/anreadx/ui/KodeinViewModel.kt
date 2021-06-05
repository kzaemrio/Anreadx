package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kodein.di.compose.instance


@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1, P2) -> VM): VM {
    return vmKodein(block.curry())
}

@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> VM): VM {
    return vmKodein(block.factory())
}

@JvmName("vmKodeinP1P2FactoryVm")
@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> () -> VM): VM {
    val p1: P1 by instance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinP1FactoryVm")
@Composable
inline fun <reified P1, reified VM : ViewModel> vmKodein(block: (P1) -> () -> VM): VM {
    val p1: P1 by instance()
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

inline fun <reified P1, reified P2, reified R> ((P1) -> (P2) -> R).factory(): (P1) -> (P2) -> () -> R {
    return { p1 -> { p2 -> { this(p1)(p2) } } }
}

inline fun <reified P1, reified P2, reified R> ((P1, P2) -> R).curry(): (P1) -> (P2) -> R {
    return { p1 -> { p2 -> this(p1, p2) } }
}
