package com.shurrikann.myapplication.myinterface

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

//定义接口(使用注解标记要转换的Data class)
interface ApiService {
    @GET
    suspend fun <T> get(
        @Url url: String,
        @QueryMap params: Map<String, String> = emptyMap()
    ): ResponseBody//返回ResponseBody 方便后续统一处理转换

    @POST
    suspend fun postJson(
        @Url url: String,
        @Body body: RequestBody
    ): ResponseBody

    @Multipart
    @POST
    suspend fun uploadFile(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): ResponseBody

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): ResponseBody
}