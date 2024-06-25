package com.kz.anreadx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import com.kz.anreadx.ui.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Route()
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun Route() {
    val animations = RootNavGraphDefaultAnimations(
        enterTransition = { slideIn() { IntOffset(it.width, 0) } },
        exitTransition = { slideOut() { IntOffset(-it.width, 0) } },
        popEnterTransition = { slideIn() { IntOffset(-it.width, 0) } },
        popExitTransition = { slideOut() { IntOffset(it.width, 0) } },
    )
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        engine = rememberAnimatedNavHostEngine(rootDefaultAnimations = animations)
    )
}
