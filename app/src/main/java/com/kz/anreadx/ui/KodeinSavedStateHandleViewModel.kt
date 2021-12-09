package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.kz.anreadx.di.curry
import org.kodein.di.compose.rememberInstance


@Composable
inline fun <reified P1, reified P2, reified P3, reified VM : ViewModel> vmKodein(block: (P1, P2, P3, SavedStateHandle) -> VM): VM {
    return vmKodein(block.curry())
}

@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1, P2, SavedStateHandle) -> VM): VM {
    return vmKodein(block.curry())
}

@JvmName("vmKodeinP1P2P3SavedStateHandle")
@Composable
inline fun <reified P1, reified P2, reified P3, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> (P3) -> (SavedStateHandle) -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinP1P2SavedStateHandle")
@Composable
inline fun <reified P1, reified P2, reified VM : ViewModel> vmKodein(block: (P1) -> (P2) -> (SavedStateHandle) -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinP1SavedStateHandle")
@Composable
inline fun <reified P1, reified VM : ViewModel> vmKodein(block: (P1) -> (SavedStateHandle) -> VM): VM {
    val p1: P1 by rememberInstance()
    return vmKodein(block(p1))
}

@JvmName("vmKodeinSavedStateFactoryVm")
@Composable
inline fun <reified VM : ViewModel> vmKodein(crossinline block: (SavedStateHandle) -> VM): VM {
    val registryOwner: NavBackStackEntry = LocalViewModelStoreOwner.current as NavBackStackEntry
    return viewModel(factory = object : AbstractSavedStateViewModelFactory(
        registryOwner,
        registryOwner.arguments
    ) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST")
            return block(handle) as T
        }
    })
}
