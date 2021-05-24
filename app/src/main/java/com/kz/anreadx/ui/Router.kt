package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*

@Composable
fun Router() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Router.FeedList.route) {
        composable(Router.FeedList.route) {
            FeedList(navToDetail = { link ->
                navController.navigate(Router.FeedDetail.buildRoute(link))
            })
        }

        composable(
            Router.FeedDetail.route,
            arguments = listOf(Router.FeedDetail.arg())
        ) {
            FeedDetail(Router.FeedDetail.argOf(it))
        }
    }
}

private object Router {
    object FeedList {
        const val route = "FeedList"
    }

    object FeedDetail {
        private const val base = "FeedDetail"
        private const val paramKey = "link"

        const val route = "$base/{$paramKey}"

        fun buildRoute(link: String): String = "$base/$link"

        fun arg(): NamedNavArgument = navArgument(paramKey) {
            type = NavType.StringType
        }

        fun argOf(entry: NavBackStackEntry): String = entry.arguments!!.getString(paramKey)!!
    }
}
