package com.kz.anreadx.model

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class RssXmlFactory constructor(private val rssXmlConverter: RssXmlConverter) :
    Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == Rss::class.java) {
            rssXmlConverter
        } else {
            super.responseBodyConverter(type, annotations, retrofit)
        }
    }
}
