package com.kz.anreadx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kz.anreadx.R
import com.kz.anreadx.ktx.ifFalse
import com.kz.anreadx.model.RssXmlConverter
import com.kz.anreadx.model.RssXmlFactory
import com.kz.anreadx.model.RssXmlParser
import com.kz.anreadx.network.RssService
import com.kz.anreadx.repository.MainRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.instance
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.create

private val xml = DI.Module("xml") {
    bindSingleton { RssXmlParser() }

    bindSingleton { RssXmlConverter(instance()) }

    bindSingleton { RssXmlFactory(instance()) }
}

private val retrofit = DI.Module("retrofit") {
    bindSingleton<RssService> {
        Retrofit.Builder()
            .baseUrl("https://www.ithome.com")
            .client(instance())
            .addConverterFactory(instance())
            .build()
            .create()
    }

    bindSingleton {
        OkHttpClient.Builder()
            .addNetworkInterceptor(instance<Interceptor>())
            .build()
    }

    bindSingleton<Interceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }
}

private val di: DI.MainBuilder.() -> Unit = {
    importOnce(xml)
    importOnce(retrofit)
    bindSingleton { MainViewModel(instance(), instance()) }
    bindSingleton { MainRepository(instance(), instance(), instance()) }
}

@Composable
fun FeedList(navToDetail: (String) -> Unit) = subDI(diBuilder = di) {

    val viewModel: MainViewModel by instance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = viewModel::clearAll) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_clear_all_24),
                            contentDescription = stringResource(id = R.string.menu_clear_all)
                        )
                    }
                }
            )
        }
    ) {
        val state = rememberSwipeRefreshState(viewModel.isLoading)
        SwipeRefresh(state = state, onRefresh = {
            state.isRefreshing.ifFalse { viewModel.updateList() }
        }) {
            val onItemClick: (String) -> Unit = {
                viewModel.read(it)
                navToDetail(it)
            }
            FeedList(
                list = viewModel.list,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun FeedList(list: List<ViewItem>, onItemClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        items(items = list, key = { it.id }) {
            Item(item = it, onItemClick)
        }
    }
}

@Composable
fun Item(item: ViewItem, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Column(Modifier.weight(1F)) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (item.done) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.timeLabel,
                style = MaterialTheme.typography.body2,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.content,
                style = MaterialTheme.typography.body2,
                color = if (item.done) Color.LightGray else Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.width(14.dp))
    }
}
