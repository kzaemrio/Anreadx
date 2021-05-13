package com.kz.anreadx.ui

import com.kz.anreadx.model.Feed
import com.kz.anreadx.model.XMLLexer
import com.kz.anreadx.model.feedTimeLabel
import org.antlr.v4.runtime.CharStreams
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

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
            var token = nextToken()
            while (token.type != XMLLexer.EOF) {
                if (token.type == XMLLexer.TEXT) {
                    yield(token.text)
                }
                token = nextToken()
            }
        }
    }
    .take(5)
    .joinToString { it }
    .run { substring(0..minOf(80, lastIndex)) }

private fun timeLabel(feed: Feed): String = feed.feedTimeLabel()
private fun title(feed: Feed): String = feed.title

private fun id(feed: Feed): String = feed.link
