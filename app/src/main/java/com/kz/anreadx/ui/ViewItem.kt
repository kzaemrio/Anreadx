package com.kz.anreadx.ui

import com.kz.anreadx.model.Feed
import com.kz.anreadx.model.feedTimeLabel
import com.kz.anreadx.xml.XMLLexer
import org.antlr.v4.runtime.CharStreams

data class ViewItem(
    val id: String,
    val title: String,
    val timeLabel: String,
    val content: String,
    val done: Boolean
)

fun ViewItem(feed: Feed) = ViewItem(
    id = id(feed),
    title = title(feed),
    timeLabel = timeLabel(feed),
    content = content(feed),
    done = done(feed)
)

fun done(feed: Feed): Boolean = feed.done

private fun content(feed: Feed): String = feed.run { description }
    .run { CharStreams.fromString(this) }
    .run { XMLLexer(this) }
    .run {
        sequence {
            while (true) {
                val nextToken = nextToken()
                when (nextToken.type) {
                    XMLLexer.EOF -> break
                    XMLLexer.TEXT -> yield(nextToken.text)
                }
            }
        }
    }
    .take(5)
    .joinToString { it }
    .run { substring(0..minOf(80, lastIndex)) }

private fun timeLabel(feed: Feed): String = feed.feedTimeLabel()
private fun title(feed: Feed): String = feed.title

private fun id(feed: Feed): String = feed.link
