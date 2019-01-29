package com.wanzi.wechatrecord.util

import okhttp3.Call
import okhttp3.Response
import java.lang.Exception

abstract class AbstractCallback{
    abstract fun succeed(call: Call?, response: Response)

    abstract fun failed(call: Call?, e: Exception?)
}
