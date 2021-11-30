package com.kz.anreadx.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.kz.anreadx.model.Rss
import okhttp3.ResponseBody as ServerError
import retrofit2.http.GET

interface RssService {
    @GET("rss")
    suspend fun rss(): NetworkResponse<Rss, ServerError>
}
