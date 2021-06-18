package com.kz.anreadx.model

import com.gitlab.mvysny.konsumexml.Konsumer
import com.gitlab.mvysny.konsumexml.konsumeXml
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.InputStream

class RssXmlParser {
    fun parse(inputStream: InputStream): Rss = inputStream.use { stream ->
        return stream.konsumeXml().child("rss") { rss(this) }
    }

    private fun rss(k: Konsumer): Rss {
        return k.guard("rss") {
            Rss(child("channel") { channel(this) })
        }
    }

    private fun channel(k: Konsumer): Channel {
        return k.guard("channel") {
            Channel(
                childText("title").apply {
                    child("language") { skipContents() }
                    child("pubDate") { skipContents() }
                    child("generator") { skipContents() }
                    child("description") { skipContents() }
                },
                childText("link"),
                children("item") { feed(this) }
            )
        }
    }

    private fun feed(k: Konsumer): Feed {
        return k.guard("item") {
            Feed(
                childText("title"),
                childText("description"),
                childText("link").apply {
                    child("guid") { skipContents() }
                },
                toLong(childText("pubDate")),
                false,
            )
        }
    }

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

    private inline fun <T> Konsumer.guard(name: String, block: Konsumer.() -> T): T {
        checkCurrent(name)
        return block()
    }
}
