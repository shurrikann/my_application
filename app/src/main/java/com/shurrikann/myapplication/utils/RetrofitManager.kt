package com.shurrikann.myapplication.utils

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shurrikann.myapplication.myinterface.ApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private const val BASE_URL = "https://your-api.com"

    //1配置kotlinx.serialization
    val jsonConfig = Json {
        ignoreUnknownKeys = true //重点，忽略后端多余字段
        isLenient = true //允许格式不规范的JSON
        encodeDefaults = true //编码时包含默认值
    }

    //2配置OkHttp

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    //初始化Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)

    //辅助工具方法
    //快速将String转为RequestBody(用于POST JSON)
    fun String.toTextBody(): RequestBody = this.toRequestBody("text/plain".toMediaType())

    fun String.toJsonBody(): RequestBody =
        this.toRequestBody("application/json; charset=utf-8".toMediaType())


    //增加上传图片的方法
    fun prepareFilePart(partName:String,file: File): MultipartBody.Part{
        //1.创建RequestBody.根据文件后缀设置MediaType
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        //2.包装成MultipartBody.Part,partName 必须与后端接口参数名一致
        return MultipartBody.Part.createFormData(partName,file.name,requestFile)
    }
}