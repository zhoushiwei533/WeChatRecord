package com.wanzi.wechatrecord

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeBootBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val i = Intent(context, CoreService::class.java)
            context.startService(i)
        }
    }