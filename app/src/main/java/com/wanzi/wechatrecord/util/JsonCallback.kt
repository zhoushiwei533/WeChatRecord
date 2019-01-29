package com.wanzi.wechatrecord.util

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.wanzi.wechatrecord.entry.BaseResponse

import okhttp3.Call
import okhttp3.Response
import java.io.IOException

abstract class JsonCallback() : AbstractCallback() {

    val TAG = JsonCallback::class.java.simpleName

    abstract fun succeed(call: Call?, data: BaseResponse)

    override fun succeed(call: Call?, response: Response) {
        try {
            if (call!!.isCanceled()) {
                failed(call, IOException("Canceled!"))
                return
            }
            Log.e(TAG,"request:" + call.request().toString())
            Log.e(TAG,"response:" + response.toString())
            if (response.code() !in 200..299) {
                failed(call, IOException("request failed , reponse's code is : " + response.code()))
                return
            }
            val str = response.body()!!.string()
            Log.e(TAG,str)
            succeed(call, Gson().fromJson(str, BaseResponse::class.java))
        } catch (e: Exception) {
            Log.e(TAG,e.toString() + "")
            failed(call, e)
        } finally {
            if (response.body() != null)
                response.body()!!.close()
        }
    }

}