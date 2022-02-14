package com.kz.anreadx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kz.anreadx.dispatcher.Background
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
    private val background: Background,
    private val feedDao: FeedDao,
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<List<DetailItem>> = MutableStateFlow(emptyList())
    val stateFlow: StateFlow<List<DetailItem>>
        get() = _stateFlow

    fun query(link: String) {
        viewModelScope.launch {
            feedDao.read(link)
            val list = withContext(background) {
                val description = feedDao.query(link).description
                val lexer = HTMLLexer(CharStreams.fromString(description))
                detailItemFlow(lexer).toList()
            }
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
