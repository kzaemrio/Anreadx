package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.kz.anreadx.di.di
import org.kodein.di.compose.subDI
import kotlin.streams.toList

@Composable
fun Router() = subDI(diBuilder = di) {
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
            FeedDetail(
                link = Router.FeedDetail.argOf(it),
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}

object Router {
    object FeedList {
        const val route = "FeedList"
    }

    object FeedDetail {
        private const val base = "FeedDetail"
        private const val argKey = "link"

        const val route = "$base/{$argKey}"

        // url link cannot be arg
        fun routeOf(link: String): String = "$base/${serial(link)}"

        private fun serial(link: String): String = link.chars()
            .toList()
            .joinToString(separator = "[")

        fun parse(charListString: String) = charListString.split("[")
            .map { it.toInt() }
            .map { it.toChar() }
            .joinToString(separator = "")

        fun arg(): NamedNavArgument = navArgument(argKey) {
            type = NavType.StringType
        }

        fun argOf(entry: NavBackStackEntry): String = entry.arguments!!.getString(argKey)!!
    }
}
