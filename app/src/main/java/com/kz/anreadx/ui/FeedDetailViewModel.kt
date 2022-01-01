package com.kz.anreadx.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kz.anreadx.EXTRA_LINK
import com.kz.anreadx.dispatcher.CPU
import com.kz.anreadx.dispatcher.DB
import com.kz.anreadx.ktx.ifTrue
import com.kz.anreadx.persistence.FeedDao
import com.kz.anreadx.xml.HTMLLexer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import org.antlr.v4.runtime.CharStreams
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val db: DB,
    private val cpu: CPU,
    private val feedDao: FeedDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val link: String = savedStateHandle.get<String>(EXTRA_LINK)!!

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
                            HTMLLexer.ATTVALUE_VALUE -> token.text.let {
                                it.contains("https") and it.uppercase().let { upper ->
                                    upper.contains("JPG") or upper.contains("PNG")
                                }
                            }.apply {
                                ifTrue {
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
