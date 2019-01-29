package com.wanzi.wechatrecord.util



import com.google.gson.Gson
import com.wanzi.wechatrecord.entry.BaseResponse
import okhttp3.*
import java.io.IOException

class RequestManager {

    var okHttpClient: OkHttpClient? = null
    var builder: Request.Builder? = null

    init {
        builder = Request.Builder()
    }

    constructor() {
        okHttpClient = HttpManager.instace().httpClient
    }

    constructor(okHttpClient: OkHttpClient?) {
        if (okHttpClient == null)
            RequestManager()
        else
            this.okHttpClient = okHttpClient
    }

    fun doGet(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null): BaseResponse? {
        if (url.isBlank())
            return null
        if (headers != null)
            addHeaders(headers)
        if (params != null) setGetParams(url, params)
        val request = Request.Builder()
                .url(if (params != null) setGetParams(url, params) else url)
                .get()
                .build()

        val call = okHttpClient!!.newCall(request)
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            val string = body!!.string()
            return Gson().fromJson(string, BaseResponse::class.java)
        }
        return null
    }

    fun doPost(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null): RequestManager {
        if (url.isBlank())
            return this
        if (headers != null)
            addHeaders(headers)
        builder!!.url(url)
        builder!!.post(setPostParams(params))
        return this
    }

    fun doPostJson(url: String, headers: HashMap<String, String>? = null, params: Any? = null): BaseResponse?  {
        if (url.isBlank())
            return null
        if (headers != null)
            addHeaders(headers)
        builder!!.url(url)
        if(params!=null) {
            var json = Gson().toJson(params)
            var requestBody:RequestBody=FormBody.create(MediaType.parse("application/json"), json)
            val request = builder!!.post(requestBody).build()
            val call = okHttpClient!!.newCall(request)
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                val string = body!!.string()
                return Gson().fromJson(string, BaseResponse::class.java)
            }
        }

        return null
    }


    fun addHeaders(headers: HashMap<String, String>) {
        headers.entries.forEach { entry ->
            headers.keys
            builder?.addHeader(entry.key, entry.value)
        }
    }

    fun setGetParams(url: String, params: HashMap<String, String>): String {
        var sb = StringBuilder(url)
        if (params.isNotEmpty()) sb.append("?") else sb
        params.forEach { entry ->
            params.keys
            sb.append(entry.key + "=" + entry.value + "&")
        }
        return if (sb.toString().endsWith("&")) sb.subSequence(0, sb.lastIndex).toString() else sb.toString()
    }

    fun setPostParams(params: HashMap<String, String>?): RequestBody? {
        var builder = FormBody.Builder()
        params?.forEach { entry ->
            params.keys
            builder.add(entry.key, entry.value)
        }
        return builder.build()
    }

    fun execute(abstractCallback: AbstractCallback) {
        okHttpClient!!.newCall(builder!!.build())?.enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                abstractCallback.succeed(call, response)
            }

            override fun onFailure(call: Call?, e: IOException?) {
                abstractCallback.failed(call, e)
            }
        })
    }
}