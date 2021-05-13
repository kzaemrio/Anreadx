package com.kz.anreadx.network

import com.kz.anreadx.model.Rss
import retrofit2.http.GET

interface RssService {
    @GET("rss")
    suspend fun request(): Rss
}
