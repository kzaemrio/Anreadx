package com.kz.anreadx.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.room.Room
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.dispatcher.DispatcherSwitch
import com.kz.anreadx.dispatcher.IO
import com.kz.anreadx.persistence.AppDatabase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.streams.toList

private val dispatcher = DI.Module(name = "dispatcher") {
    bindSingleton { DispatcherSwitch(instance(), instance(), instance()) }
    bindSingleton { DB(instance()) }
    bindSingleton { IO() }
    bindSingleton { CPU() }
    bindSingleton<Executor> {
        Executors.newSingleThreadExecutor {
            Thread(it, "db-single-thread")
        }
    }
}

private val room = DI.Module(name = "room") {
    bindSingleton { instance<AppDatabase>().itemDao() }
    bindSingleton {
        Room.databaseBuilder(
            instance(),
            AppDatabase::class.java,
            "room-db"
        ).setQueryExecutor(instance()).setQueryExecutor(instance()).build()
    }
}

private val di: DI.MainBuilder.() -> Unit = {
    importOnce(dispatcher)
    importOnce(room)
}

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
        private const val argKey = "link"

        const val route = "$base/{$argKey}"

        // url link cannot be arg
        fun routeOf(link: String): String = "$base/${link.chars().toList()}"

        fun arg(): NamedNavArgument = navArgument(argKey) {
            type = NavType.StringType
        }

        fun argOf(entry: NavBackStackEntry): String = entry.arguments!!.getString(argKey)!!
    }
}
