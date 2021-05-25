package com.kz.anreadx.model

import com.gitlab.mvysny.konsumexml.konsumeXml
import java.io.InputStream
import javax.inject.Inject

class RssXmlParser @Inject constructor() {
    fun parse(inputStream: InputStream): Rss = inputStream.use { stream ->
        return stream.konsumeXml().child("rss") { Rss.xml(this) }
    }
}
