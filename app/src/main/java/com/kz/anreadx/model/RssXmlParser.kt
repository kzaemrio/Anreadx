package com.kz.anreadx.model

import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.konsumeXml
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.InputStream

class RssXmlParser {
    fun parse(inputStream: InputStream): Rss = inputStream.use { stream ->
        return stream.konsumeXml().child("rss") { Rss() }
    }

    private fun Konsumer.Rss(): Rss = Rss(child("channel") { Channel() })

    private fun Konsumer.Channel(): Channel = Channel(
        childText("title").apply {
            child("language") { skipContents() }
            child("pubDate") { skipContents() }
            child("generator") { skipContents() }
            child("description") { skipContents() }
        },
        childText("link"),
        children("item") { Feed() }
    )

    private fun Konsumer.Feed(): Feed = Feed(
        childText("title"),
        childText("description"),
        childText("link").apply {
            child("guid") { skipContents() }
        },
        toLong(childText("pubDate")),
        false,
    )

    private fun toLong(trim: String): Long {
        val originalZonedDateTime = getZonedDateTime(trim)
        val fixedZonedDateTime = originalZonedDateTime.withZoneSameInstant(
            ZoneId.systemDefault()
        )
        return fixedZonedDateTime.toInstant().toEpochMilli()
    }

    private fun getZonedDateTime(time: String): ZonedDateTime {
        return try {
            ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME)
        }
    }
}
