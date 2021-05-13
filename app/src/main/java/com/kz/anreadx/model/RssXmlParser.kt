package com.kz.anreadx.model

import android.util.Xml
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject

private object Tag {
    const val item = "item"
    const val description = "description"
    const val pubDate = "pubDate"
    const val link = "link"
    const val title = "title"
    const val channel = "channel"
    const val rss = "rss"
    val ns: String? = null
}

class RssXmlParser @Inject constructor() {
    fun parse(inputStream: InputStream): Rss = inputStream.use { stream ->
        val pullParser = Xml.newPullParser().apply {
            setInput(stream, null)
            nextTag()
        }
        return reedRss(pullParser)
    }

    private fun reedRss(parser: XmlPullParser): Rss {
        parser.require(XmlPullParser.START_TAG, Tag.ns, Tag.rss)
        while (parser.nextNotEndTag) {
            if (parser.currentNotStartTag) {
                continue
            }
            if (parser.name == Tag.channel) {
                return Rss(reedChannel(parser))
            } else {
                parser.skip()
            }
        }
        throw IllegalArgumentException("parse xml rss error")
    }

    private fun reedChannel(parser: XmlPullParser): Channel {
        parser.require(XmlPullParser.START_TAG, Tag.ns, Tag.channel)
        var title: String? = null
        var link: String? = null
        val list = mutableListOf<Feed>()

        while (parser.nextNotEndTag) {
            if (parser.currentNotStartTag) {
                continue
            }
            when (parser.name) {
                Tag.title -> title = readText(parser, Tag.title)
                Tag.link -> link = readText(parser, Tag.link)
                Tag.item -> list += readItem(parser)
                else -> {
                    parser.skip()
                }
            }
        }
        return Channel(title!!, link!!, list)
    }


    private fun readItem(parser: XmlPullParser): Feed {
        parser.require(XmlPullParser.START_TAG, Tag.ns, Tag.item)
        var title: String? = null
        var link: String? = null
        var pubDate: String? = null
        var description: String? = null
        while (parser.nextNotEndTag) {
            if (parser.currentNotStartTag) {
                continue
            }
            when (parser.name) {
                Tag.title -> title = readText(parser, Tag.title)
                Tag.link -> link = readText(parser, Tag.link)
                Tag.pubDate -> pubDate = readText(parser, Tag.pubDate)
                Tag.description -> description = readText(parser, Tag.description)
                else -> {
                    parser.skip()
                }
            }
        }
        return Feed(title!!, link!!, toLong(pubDate!!), description!!)
    }

    private fun toLong(trim: String): Long {
        val originalZonedDateTime = getZonedDateTime(trim)
        val fixedZonedDateTime = originalZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        return fixedZonedDateTime.toInstant().toEpochMilli()
    }

    private fun getZonedDateTime(time: String): ZonedDateTime {
        return try {
            ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME)
        }
    }

    private fun readText(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, Tag.ns, tag)
        val result = readText(parser)
        parser.require(XmlPullParser.END_TAG, Tag.ns, tag)
        return result
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

}

val XmlPullParser.currentNotStartTag: Boolean
    get() {
        return eventType != XmlPullParser.START_TAG
    }

val XmlPullParser.nextNotEndTag: Boolean
    get() {
        return next() != XmlPullParser.END_TAG
    }

fun XmlPullParser.skip() {
    if (currentNotStartTag) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}
