package com.kz.anreadx.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.kz.anreadx.R
import com.kz.anreadx.dispatcher.DispatcherSwitch
import com.kz.anreadx.ktx.ifTrue
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.xml.HTMLLexer
import org.antlr.v4.runtime.CharStreams
import org.kodein.di.compose.instance

@Composable
fun FeedDetail(link: String, onBackClick: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = stringResource(id = R.string.menu_clear_all)
                    )
                }
            }
        )
    }) {
        FeedDetailList(Router.FeedDetail.parse(link))
    }
}

@Composable
fun FeedDetailList(link: String) {
    val (list, setList) = remember {
        mutableStateOf(emptyList<Content>())
    }

    val dispatcher: DispatcherSwitch by instance()
    val feedDao: FeedDao by instance()

    LaunchedEffect(link) {
        setList(loadContentList(dispatcher, feedDao, link))
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(list) {
            when (it) {
                is Content.Image -> Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    painter = rememberCoilPainter(it.url),
                    contentDescription = it.url,
                )
                is Content.Text -> Text(text = it.value)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private suspend fun loadContentList(
    dispatcher: DispatcherSwitch,
    feedDao: FeedDao,
    link: String
): List<Content> {
    val feed = dispatcher.db { feedDao.query(link) }
    val sequence = dispatcher.cpu {
        sequence {
            val lexer = HTMLLexer(CharStreams.fromString(feed.description))
            while (true) {
                val token = lexer.nextToken()
                when (token.type) {
                    HTMLLexer.EOF -> break
                    HTMLLexer.HTML_TEXT -> yield(Content.Text(token.text))
                    HTMLLexer.ATTVALUE_VALUE -> token.text.apply {
                        contains("https").ifTrue {
                            yield(
                                Content.Image(
                                    token.text.replace(
                                        "\"",
                                        ""
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    return sequence.toList()
}

sealed class Content {
    data class Image(val url: String) : Content()
    data class Text(val value: String) : Content()
}
