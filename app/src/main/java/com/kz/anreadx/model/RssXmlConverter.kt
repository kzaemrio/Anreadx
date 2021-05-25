package com.kz.anreadx.model

import okhttp3.ResponseBody
import retrofit2.Converter

class RssXmlConverter constructor(private val rssXmlParser: RssXmlParser) :
    Converter<ResponseBody, Rss> {
    override fun convert(value: ResponseBody): Rss {
        return rssXmlParser.parse(value.byteStream())
    }
}
