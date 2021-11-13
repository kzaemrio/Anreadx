package com.kz.anreadx.ui

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kz.anreadx.di.routerDi
import org.kodein.di.compose.subDI

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Router() = subDI(diBuilder = routerDi) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Router.FeedList.route,
        enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }) },
        exitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it / 2 }) },
        popEnterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { -it / 2 }) },
        popExitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { it / 2 }) }
    ) {
        composable(Router.FeedList.route) {
            FeedList(navToDetail = { link ->
                navController.navigate(Router.FeedDetail.routeOf(link))
            })
        }

        composable(Router.FeedDetail.route) {
            FeedDetail(onBackClick = {
                navController.popBackStack()
            })
        }
    }
}

object Router {
    object FeedList {
        const val route = "FeedList"
    }

    object FeedDetail {
        private const val base = "FeedDetail"

        const val argKey = "link"

        const val route = "$base/{$argKey}"

        // url link cannot be arg
        fun routeOf(link: String): String = "$base/${serial(link)}"

        private fun serial(link: String): String = link.replace('/', '$')

        fun parse(link: String) = link.replace('$', '/')
    }
}
