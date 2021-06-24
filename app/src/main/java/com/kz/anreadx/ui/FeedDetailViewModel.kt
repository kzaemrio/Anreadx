package com.kz.anreadx.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.ktx.ifTrue
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.xml.HTMLLexer
import kotlinx.coroutines.flow.*
import org.antlr.v4.runtime.CharStreams

class FeedDetailViewModel(
    private val db: DB,
    private val cpu: CPU,
    private val feedDao: FeedDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val link by lazy {
        savedStateHandle.get<String>(Router.FeedDetail.argKey)!!.let {
            Router.FeedDetail.parse(it)
        }
    }

    val detailItemList: Flow<List<DetailItem>>
        get() = flow { emit(link) }
            .map { feedDao.query(it) }
            .flowOn(db)
            .map { it.description }
            .map { HTMLLexer(CharStreams.fromString(it)) }
            .map { lexer ->
                flow {
                    while (true) {
                        val token = lexer.nextToken()
                        when (token.type) {
                            HTMLLexer.EOF -> break
                            HTMLLexer.HTML_TEXT -> emit(DetailItem.Text(token.text))
                            HTMLLexer.ATTVALUE_VALUE -> token.text.apply {
                                contains("https").ifTrue {
                                    emit(
                                        DetailItem.Image(
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
                }.toList()
            }
            .flowOn(cpu)
}
