package com.kz.anreadx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.kz.anreadx.ui.FeedDetail
import com.kz.anreadx.ui.FeedList
import com.kz.anreadx.ui.NavGraphs
import com.kz.anreadx.ui.destinations.FeedDetailDestination
import com.kz.anreadx.ui.destinations.FeedListDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialNavigationApi
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

@ExperimentalMaterialNavigationApi
@ExperimentalAnimationApi
@Composable
fun Route() {
    val engine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { slideIn() { IntOffset(it.width, 0) } },
            exitTransition = { slideOut() { IntOffset(-it.width, 0) } },
            popEnterTransition = { slideIn() { IntOffset(-it.width, 0) } },
            popExitTransition = { slideOut() { IntOffset(it.width, 0) } },
        )
    )
    val controller = rememberAnimatedNavController()
    DestinationsNavHost(navGraph = NavGraphs.root, engine = engine, navController = controller) {
        composable(FeedListDestination) {
            FeedList(onItemClick = { controller.navigate(FeedDetailDestination(link = it)) })
        }

        composable(FeedDetailDestination) {
            FeedDetail(link = navArgs.link, onBackClick = { controller.navigateUp() })
        }
    }
}
