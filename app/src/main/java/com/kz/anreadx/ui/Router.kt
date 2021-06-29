package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.kz.anreadx.di.routerDi
import org.kodein.di.compose.subDI

@Composable
fun Router() = subDI(diBuilder = routerDi) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Router.FeedList.route) {
        composable(Router.FeedList.route) {
            FeedList(navToDetail = { link ->
                navController.navigate(Router.FeedDetail.routeOf(link))
            })
        }

        composable(
            Router.FeedDetail.route,
            arguments = listOf(Router.FeedDetail.arg())
        ) {
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

        fun arg(): NamedNavArgument = navArgument(argKey) {
            type = NavType.StringType
        }

        fun argOf(entry: NavBackStackEntry): String = entry.arguments!!.getString(argKey)!!
    }
}
