package com.kz.anreadx.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.EXTRA_LINK
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.xml.HTMLLexer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.antlr.v4.runtime.CharStreams
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    db: DB,
    cpu: CPU,
    feedDao: FeedDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<List<DetailItem>> = MutableStateFlow(emptyList())
    val stateFlow: StateFlow<List<DetailItem>>
        get() = _stateFlow

    init {
        val link: String = savedStateHandle.get<String>(EXTRA_LINK)!!
        viewModelScope.launch {
            val feed = withContext(db) {
                feedDao.query(link)
            }
            val lexer = HTMLLexer(CharStreams.fromString(feed.description))
            val detailItemFlow = detailItemFlow(lexer)
            val list = detailItemFlow.flowOn(cpu).toList()
            _stateFlow.emit(list)
        }
    }

    private fun detailItemFlow(lexer: HTMLLexer): Flow<DetailItem> = flow {
        while (true) {
            val token = lexer.nextToken()
            val text = token.text
            when (token.type) {
                HTMLLexer.EOF -> {
                    break
                }
                HTMLLexer.HTML_TEXT -> {
                    emit(DetailItem.Text(text))
                }
                HTMLLexer.ATTVALUE_VALUE -> {
                    if (text.contains("https")) {
                        val uppercase = text.uppercase()
                        if (uppercase.contains("JPG") or uppercase.contains("PNG")) {
                            emit(DetailItem.Image(text.replace("\"", "")))
                        }
                    }
                }
            }
        }
    }
}
