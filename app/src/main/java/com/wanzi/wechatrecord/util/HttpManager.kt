package com.wanzi.wechatrecord.util


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class HttpManager private constructor() {

    var httpClient: OkHttpClient? = null
    val timeUnit: TimeUnit = TimeUnit.SECONDS
    val connectTimeOut: Long = 20
    val readTimeOut: Long = 20
    val writeTimeOut: Long = 20
    var interceptors: ArrayList<Interceptor> = ArrayList()
    var networkInterceptors: ArrayList<Interceptor> = ArrayList()

    companion object {
        fun instace() = Holder.INSTACE
    }

    private object Holder {
        val INSTACE = HttpManager()

        init {
            INSTACE.initHttpClient()
        }
    }

    fun initHttpClient(): OkHttpClient? {
        var builder = OkHttpClient.Builder()
                .connectTimeout(connectTimeOut, timeUnit)
                .readTimeout(readTimeOut, timeUnit)
                .writeTimeout(writeTimeOut, timeUnit)
        builder = initInterceptor(builder, interceptors)
        builder = initNetworkInterceptor(builder, networkInterceptors)
        httpClient = builder.build()
        return httpClient
    }

    fun addInterceptor(interceptors: ArrayList<Interceptor>) {
        this.interceptors = interceptors
    }

    fun addNetworkInterceptor(interceptors: ArrayList<Interceptor>) {
        this.networkInterceptors = interceptors
    }

    fun initInterceptor(builder: OkHttpClient.Builder?, interceptors: ArrayList<Interceptor>?): OkHttpClient.Builder? {
        interceptors?.forEach { i: Interceptor? ->
            if (i != null)
                builder?.addInterceptor(i)
        }
        return builder
    }

    fun initNetworkInterceptor(builder: OkHttpClient.Builder?, networkInterceptors: ArrayList<Interceptor>?): OkHttpClient.Builder? {
        networkInterceptors?.forEach { i: Interceptor? ->
            if (i != null)
                builder?.addNetworkInterceptor(i)
        }
        return builder
    }

}
